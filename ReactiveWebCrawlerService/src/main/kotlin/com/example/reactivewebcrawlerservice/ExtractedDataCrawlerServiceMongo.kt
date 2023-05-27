package com.example.reactivewebcrawlerservice

import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class ExtractedDataCrawlerServiceMongo(
    private val extractedDataCrud: ExtractedDataCrud,
) : ExtractedDataCrawlerService{

    override fun runCrawler(url:String) {
        val playwright = Playwright.create()
        try {
            val browser = playwright.chromium().launch()
            val context = browser.newContext()
            val page = context.newPage()
            page.navigate(url)
            var cssString = "css-1vxp1x8"
            if (url.contains("twitter")){
                cssString = "css-1gnf693"
            }
            if (url.contains("darkweb")){
                cssString = "css-8kdga4"
            }
            var posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.${cssString}")
            while (posts.isEmpty()) {
                page.waitForSelector("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.${cssString}")
                posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.${cssString}")
            }
            val postDivs = mutableMapOf<Int, List<ElementHandle>>()
            for ((i, post) in posts.withIndex()) {
                postDivs[i + 1] = post.querySelectorAll("div")
            }
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss")
//           save each post at our mongo db
            for ((k, v) in postDivs) {
                println("$k post. from${url}")
                val nameDate = v[3].querySelectorAll("span")
                val name = nameDate[0].innerText()
                val dateTimeLocation = nameDate[1].innerText().split("\n")
                val dateTime = dateTimeLocation[0]
                val location = dateTimeLocation[1]
                val regex = """-?\d+\.\d+""".toRegex()
                val matchResults = regex.findAll(location)

                val numbers = matchResults.map { it.value.toDouble() }.toList()

                var latitude = numbers.getOrNull(0)
                var longitude = numbers.getOrNull(1)


                val date = LocalDateTime.parse(dateTime, formatter)
                val postContent = v[8].innerText()
                // check if entity with the same publisher and timestamp already exists
                this.extractedDataCrud.findByPublisherAndTimestamp(name,date)
                    .switchIfEmpty(
                        Mono.defer{
                            var extractedDataEntity = ExtractedDataEntity()
                            extractedDataEntity.websiteUrl = url
                            extractedDataEntity.publisher = name
                            extractedDataEntity.timestamp = date
                            extractedDataEntity.content = postContent
                            extractedDataEntity.location = latitude.toString() + "," +longitude.toString()
                            // entity does not exist, save it to the database
                            extractedDataCrud.save(extractedDataEntity)
                        }
                    )
                    .flatMap {
                        Mono.empty<Void>()
                    }
                    .subscribe(
                        {
                            println("Extracted Data Entity saved successfully")
                        },
                        {error ->
                            println("An error occurred while saving data to the database: ${error.message}")

                        }
                    )
            }

            browser.close()
        } catch (e: Exception) {
            println("An error occurred on url ${url}: ${e.message}")
        } finally {
            playwright.use { }
            playwright.close()
        }
    }

    override fun getUnpredictedData(): Flux<ExtractedDataEntity> {
        return this.extractedDataCrud.findAllByIsPredictedFalse()
    }

    override fun updateExtractedDataPredicted(id: String): Mono<Void> {
        return this.extractedDataCrud.findById(id)
            .flatMap { entity ->
                entity.isPredicted = true
                this.extractedDataCrud.save(entity)
            }
            .then()
    }
}
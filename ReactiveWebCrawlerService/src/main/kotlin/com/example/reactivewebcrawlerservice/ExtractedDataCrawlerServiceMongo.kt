package com.example.reactivewebcrawlerservice

import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Service
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
            var posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-1vxp1x8")
            while (posts.isEmpty()) {
                page.waitForSelector("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-qlht7l-MuiPaper-root-MuiCard-root")
                posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-qlht7l-MuiPaper-root-MuiCard-root")
            }
            val postDivs = mutableMapOf<Int, List<ElementHandle>>()
            for ((i, post) in posts.withIndex()) {
                postDivs[i + 1] = post.querySelectorAll("div")
            }
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss")
//           save each post at our mongo db
            for ((k, v) in postDivs) {
                println("$k.")
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
                val postContent = v[5].querySelector("p").innerText()
                // check if entity with the same publisher and timestamp already exists
                var extractedDataEntity = ExtractedDataEntity()
                extractedDataEntity.websiteUrl = url
                extractedDataEntity.publisher = name
                extractedDataEntity.timestamp = date
                extractedDataEntity.content = postContent
                extractedDataEntity.location = latitude.toString() + "," +longitude.toString()

                this.extractedDataCrud.findByPublisherAndTimestamp(name,date)
                    .switchIfEmpty(
                        Mono.defer{
                            var extractedDataEntity = ExtractedDataEntity()
                            extractedDataEntity.websiteUrl = url
                            extractedDataEntity.publisher = name
                            extractedDataEntity.timestamp = date
                            extractedDataEntity.content = postContent
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
            println("An error occurred: ${e.message}")
        } finally {
            playwright.use { }
            playwright.close()
        }
    }
}
package com.example.reactivewebcrawlerservice

import com.microsoft.playwright.ElementHandle
import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Service


@Service
class ExtractedDataCrawlerServiceMongo(
    private val extractedDataCrud: ExtractedDataCrud,
) : ExtractedDataCrawlerService{

    override fun runCrawler(url: String) {
        println("hello $url")
        val playwright = Playwright.create()
        try {
            val browser = playwright.chromium().launch()
            val context = browser.newContext()
            val page = context.newPage()
            page.navigate(url)
            var posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-qlht7l-MuiPaper-root-MuiCard-root")
            while (posts.isEmpty()) {
                page.waitForSelector("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-qlht7l-MuiPaper-root-MuiCard-root")
                posts = page.querySelectorAll("div.MuiPaper-root.MuiPaper-outlined.MuiPaper-rounded.MuiCard-root.css-qlht7l-MuiPaper-root-MuiCard-root")
            }
            val postDivs = mutableMapOf<Int, List<ElementHandle>>()
            for ((i, post) in posts.withIndex()) {
                postDivs[i + 1] = post.querySelectorAll("div")
            }
            for ((k, v) in postDivs) {
                println("$k.")
                val nameDate = v[3].querySelectorAll("span")
                val name = nameDate[0].innerText()
                val date = nameDate[1].innerText()
                val postContent = v[5].querySelector("p").innerText()
                println("name = $name")
                println("date = $date")
                println("content = $postContent")
            }
            browser.close()
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        } finally {
            playwright.use { }
            playwright.close()
        }

    //        val keywords = doc.select("meta[name=keywords]").attr("content").split(",").map { it.trim() }

//        var extractedDataEntity = ExtractedDataEntity()
//        extractedDataEntity.content = content
//        extractedDataEntity.websiteUrl = url
//        extractedDataEntity.timestamp = LocalDateTime.now()
//        extractedDataEntity.keywords = keywords


    }


}
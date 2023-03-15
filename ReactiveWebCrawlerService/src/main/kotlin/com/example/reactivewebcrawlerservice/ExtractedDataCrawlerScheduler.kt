package com.example.reactivewebcrawlerservice

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExtractedDataCrawlerScheduler(
    private val extractedDataCrawlerService: ExtractedDataCrawlerService) {
        @Scheduled(fixedRate = 10000)
        fun runCrawler() {
            val urls = listOf(
                "http://localhost:3000/facebook",
                "http://localhost:3000/reddit",
                "http://localhost:3000/telegram",
                "http://localhost:3000/twitter"
            )
            // TODO: remove the comment here
//            urls.parallelStream().forEach { url ->
//                extractedDataCrawlerService.runCrawler(url)
//            }
            extractedDataCrawlerService.runCrawler("http://localhost:3000/facebook")
        }
    }
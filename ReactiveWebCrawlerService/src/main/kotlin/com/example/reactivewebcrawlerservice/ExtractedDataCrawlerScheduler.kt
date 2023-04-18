package com.example.reactivewebcrawlerservice

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExtractedDataCrawlerScheduler(
    private val extractedDataCrawlerService: ExtractedDataCrawlerService) {
        @Scheduled(fixedRate = 1000)
        fun runCrawler() {
            val urls = listOf(
                "https://project-simulations.onrender.com/facebook",
               // "https://project-simulations.onrender.com/reddit",
                "https://project-simulations.onrender.com/darkweb",
                "https://project-simulations.onrender.com/twitter"
            )
            // TODO: remove the comment here
            urls.parallelStream().forEach { url ->
                extractedDataCrawlerService.runCrawler(url)
            }
           // extractedDataCrawlerService.runCrawler(urls)
        }
    }
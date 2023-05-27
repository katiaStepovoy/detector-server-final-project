package com.example.reactivewebcrawlerservice

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExtractedDataCrawlerScheduler(
    private val extractedDataCrawlerService: ExtractedDataCrawlerService) {
        @Scheduled(fixedRate = 10000) //10000 = 10 sec, 600000= 10min
        fun runCrawler() {
            val urls = listOf(
                "https://project-simulations.onrender.com/facebook",
                "https://project-simulations.onrender.com/reddit",
                "https://project-simulations.onrender.com/darkweb",
                "https://project-simulations.onrender.com/twitter"
            )
            urls.parallelStream().forEach { url ->
                extractedDataCrawlerService.runCrawler(url)
            }
        }
    }

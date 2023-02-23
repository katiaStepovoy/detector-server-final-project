package com.example.reactivewebcrawlerservice

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ExtractedDataCrawlerScheduler(
    private val extractedDataCrawlerService: ExtractedDataCrawlerService) {
        @Scheduled(fixedRate = 1800000) // execute every 30 minutes (30 * 60 * 1000)
        fun runCrawler() {
            extractedDataCrawlerService.runCrawler("https://example.com")
        }
    }
package com.example.reactivewebcrawlerservice

import org.springframework.web.reactive.function.client.WebClient

class ExtractedDataCrawlerServiceMongo(
    private val extractedDataCrud: ExtractedDataCrud,
    private val webClient: WebClient
) : ExtractedDataCrawlerService{

    override fun runCrawler(url: String) {
        TODO("Not yet implemented")
    }


}
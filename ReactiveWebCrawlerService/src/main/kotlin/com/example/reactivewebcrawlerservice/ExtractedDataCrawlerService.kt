package com.example.reactivewebcrawlerservice

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ExtractedDataCrawlerService {
    fun runCrawler(url: String)
    fun getUnpredictedData(): Flux<ExtractedDataEntity>
    fun updateExtractedDataPredicted(id: String): Mono<Void>
}
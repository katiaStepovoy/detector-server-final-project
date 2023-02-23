package com.example.reactivewebcrawlerservice

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface WebCrawlerService {
    fun extractData(agentUrl: String, keywords: List<String>): Flux<ExtractedDataBoundary>

}

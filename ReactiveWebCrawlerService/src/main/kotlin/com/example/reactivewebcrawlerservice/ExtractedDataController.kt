package com.example.reactivewebcrawlerservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ExtractedDataController (
    @Autowired val extractedDataCrawlerService: ExtractedDataCrawlerService
        ){

    @RequestMapping(
        path = ["/extracted"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUnpredictedData(): Flux<ExtractedDataEntity> {
        return this.extractedDataCrawlerService
            .getUnpredictedData()
    }

    @RequestMapping(
        path = ["/extracted/{extractedID}"],
        method = [RequestMethod.PUT],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateExtractedDataPredicted(@PathVariable extractedID: String):Mono<Void> {
        return this.extractedDataCrawlerService
            .updateExtractedDataPredicted(extractedID)
    }
}
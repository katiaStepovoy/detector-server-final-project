package com.example.reactivewebcrawlerservice

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface ExtractedDataCrud: ReactiveMongoRepository<ExtractedDataEntity, String> {
    fun findByPublisherAndTimestamp(name: String, date: LocalDateTime): Mono<ExtractedDataEntity>

    fun findAllByIsPredictedFalse(): Flux<ExtractedDataEntity>
}
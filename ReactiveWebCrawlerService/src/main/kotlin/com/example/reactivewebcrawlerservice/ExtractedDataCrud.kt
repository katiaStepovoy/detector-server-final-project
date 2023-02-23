package com.example.reactivewebcrawlerservice

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ExtractedDataCrud: ReactiveMongoRepository<ExtractedDataEntity, String> {
}
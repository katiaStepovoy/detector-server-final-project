package com.example.reactivewebcrawlerservice

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "EXTRACTED")
class ExtractedDataEntity() {
    @Id
    var extractedDataId: String? = null
    val content: String? = null
    val websiteUrl: String? = null
    val timestamp: LocalDateTime? = null
    val keywords: List<String>? = null
    val location: String? = null
}

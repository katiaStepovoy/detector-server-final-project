package com.example.reactivewebcrawlerservice

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "EXTRACTED_DATA")
class ExtractedDataEntity() {
    @Id
    var extractedDataId: String? = null
    var publisher: String? = null
    var content: String? = null
    var websiteUrl: String? = null
    var timestamp: LocalDateTime? = null
    var keywords: List<String>? = null
    var location: String? = null
    var isPredicted: Boolean? = false
    override fun toString(): String {
        return "ExtractedDataEntity(extractedDataId=$extractedDataId, publisher=$publisher, content=$content, websiteUrl=$websiteUrl, timestamp=$timestamp, keywords=$keywords, location=$location, isPredicted=$isPredicted)"
    }


}

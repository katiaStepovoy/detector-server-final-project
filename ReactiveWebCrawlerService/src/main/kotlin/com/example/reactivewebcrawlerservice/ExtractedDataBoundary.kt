package com.example.reactivewebcrawlerservice

import java.time.LocalDateTime
import java.util.*

class ExtractedDataBoundary() {
    var extractedDataId: String? = null
    val content: String? = null
    val websiteUrl: String? = null
    val timestamp: LocalDateTime? = null
    val keywords: List<String>? = null
    val location: String? = null

    override fun toString(): String {
        return "ExtractedDataBoundary(ExtractedDataId=$extractedDataId, content=$content, websiteUrl=$websiteUrl, timestamp=$timestamp, keywords=$keywords, location=$location)"
    }
}

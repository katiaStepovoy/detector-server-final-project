package com.example.reactivealertsmanagementservice

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "ALERTS")
class AlertEntity() {
    @Id
    var alertId: String? = null
    var website: String? = null
    var location: String? = null // should be "lat,lng"
    var timestamp: Date? = null
    var feedback: String? = null
    var content: String? = null // post's text
    var publisher: String? = null
    var keywords: List<String>? = null
}

package com.example.reactivealertsmanagementservice

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "ALERTS")
class AlertEntity() {
    @Id
    var alertId: String? = null
    var crawlerId: String? = null
    var userId: String? = null
    var website: String? = null
    var location: String? = null
    var timestamp: Date? = null
    var feedback: String? = null
    var keywords: List<String>? = null
}

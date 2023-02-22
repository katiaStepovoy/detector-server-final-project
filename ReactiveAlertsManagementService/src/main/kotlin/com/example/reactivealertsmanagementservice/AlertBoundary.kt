package com.example.reactivealertsmanagementservice

import java.util.*

class AlertBoundary() {
    var alertId: String? = null
    var crawlerId: String? = null
    var userId: String? = null
    var website: String? = null
    var location: String? = null
    var timestamp: Date? = null
    var feedback: String? = null
    var keywords: List<String>? = null

    override fun toString(): String {
        return "AlertBoundary(alertId=$alertId, crawlerId=$crawlerId, userId=$userId, website=$website, location=$location, timestamp=$timestamp, feedback=$feedback, keywords=$keywords)"
    }


}

package com.example.reactivealertsmanagementservice

import org.springframework.stereotype.Component
import java.util.*

@Component
class AlertConverter {
    fun toBoundary(entity:AlertEntity) :AlertBoundary {
        var boundary = AlertBoundary()

        boundary.alertId = entity.alertId
        boundary.website = entity.website
        boundary.location = entity.location
        boundary.timestamp = entity.timestamp
        boundary.feedback = entity.feedback
        boundary.content = entity.content
        boundary.publisher = entity.publisher
        boundary.keywords = entity.keywords
        return boundary
    }
    fun toEntity (boundary:AlertBoundary):AlertEntity {
        var entity = AlertEntity()

        if (boundary.alertId != null) {
            entity.alertId = boundary.alertId!!
        }
        entity.website = boundary.website
        entity.location = boundary.location
        entity.timestamp = boundary.timestamp
        entity.feedback = boundary.feedback
        entity.content = boundary.content
        entity.publisher = boundary.publisher
        entity.keywords = boundary.keywords

        return entity
    }


}

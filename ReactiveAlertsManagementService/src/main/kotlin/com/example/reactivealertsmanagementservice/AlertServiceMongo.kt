package com.example.reactivealertsmanagementservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AlertServiceMongo(
    @Autowired val crud:AlertCrud,
    @Autowired val converter:AlertConverter
): AlertService {

    @Transactional
    override fun create(alert: AlertBoundary): Mono<AlertBoundary> {
        alert.alertId = null
        alert.timestamp = Date()

        return Mono.just(alert)
            .log()
            .map { this.converter.toEntity(it) }
            .flatMap { this.crud.save(it) }
            .map { this.converter.toBoundary(it) }
            .log()
    }

    @Transactional(readOnly = true)
    override fun getSpecificAlert(alertId: String): Mono<AlertBoundary> {
        return this.crud
            .findById(alertId)
            .map { this.converter.toBoundary(it) }
            .log()
    }

    @Transactional
    override fun cleanup(): Mono<Void> {
        return this.crud
            .deleteAll()
            .log()
    }

    @Transactional(readOnly = true)
    override fun search(
        filterType: String,
        filterValue: String,
        sortAttribute: String,
        sortOrder: String,
        size: Int,
        page: Int
    ): Flux<AlertBoundary> {
        val pageable = PageRequest.of(page, size,
            getSortOrder(sortOrder) as Sort.Direction,getSortAttribute(sortAttribute) , "alertId")
        when (filterType) {
            "byLocation" -> {
                return this.crud
                    .findAllByLocation(filterValue, pageable)
                    .map {
                        this.converter.toBoundary(it)
                    }
                    .log()
            }
            "byWebsite" -> {
                return this.crud
                    .findAllByWebsite(filterValue, pageable)
                        .map {
                            this.converter.toBoundary(it)
                        }
                        .log()
            }
            "byTimestamp" -> {
                val timestamp = LocalDateTime.parse(filterValue, DateTimeFormatter.ofPattern("ddMMyyyyHHmm"))
                return this.crud
                    .findAllByTimestamp(Date.from(timestamp.toInstant(ZoneOffset.UTC)), pageable)
                    .map {
                        this.converter.toBoundary(it)
                    }
                    .log()
            }
            "byTimestampInRange" -> {
                val from = LocalDateTime.parse(filterValue.split(",")[0], DateTimeFormatter.ofPattern("ddMMyyyyHHmm"))
                val to = LocalDateTime.parse(filterValue.split(",")[1], DateTimeFormatter.ofPattern("ddMMyyyyHHmm"))
                return this.crud.findAllByTimestampBetween(
                    Date.from(from.toInstant(ZoneOffset.UTC)),
                    Date.from(to.toInstant(ZoneOffset.UTC)),
                    pageable)
                    .map {
                        this.converter.toBoundary(it)
                    }
            }
            "byKeywords" -> {
                return this.crud
                    .findAllByKeywordsContains(filterValue, pageable)
                    .map {
                        this.converter.toBoundary(it)
                    }
                    .log()
            }
            else->{
                if(filterType != "")
                    throw InputException("$filterType is not valid option")
                return this.crud
                    .findByAlertIdNotNull(pageable)
                    .map {
                     this.converter.toBoundary(it)
                    }
            }
        }
    }

    override fun updateAlert(alert: AlertBoundary): Mono<Void> {
        return this.crud.findById(alert.alertId!!)
            .switchIfEmpty(Mono.error(AlertNotFoundException("Visit with ID ${alert.alertId} not found.")))
            .flatMap {
                val entity = this.converter.toEntity(alert)
                entity.alertId = it.alertId // make sure to set the ID from the existing entity
                this.crud.save(entity)
            }
            .then()

    }

    private fun getSortOrder(sortOrder: String): Any {
        if (sortOrder != "DESC" && sortOrder!= "ASC")
            throw InputException("$sortOrder is not valid - either ASC or DESC")
        if(sortOrder == "DESC"){
            return  Sort.Direction.DESC
        }
        return Sort.Direction.ASC
    }

    private fun getSortAttribute(sortAttribute: String): String {
        if (sortAttribute != "alertId" &&
            sortAttribute != "crawlerId" &&
            sortAttribute != "userId" &&
            sortAttribute != "website" &&
            sortAttribute != "location" &&
            sortAttribute != "timestamp" &&
            sortAttribute != "feedback" &&
            sortAttribute != "keywords")
            throw InputException("$sortAttribute is not valid")
        return sortAttribute
    }


}
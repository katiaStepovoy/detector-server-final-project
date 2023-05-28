package com.example.reactivealertsmanagementservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
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
        if (alert.timestamp == null)
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
        filters: Map<String, String>,
        sortAttribute: String,
        sortOrder: String,
        size: Int,
        page: Int
    ): Flux<AlertBoundary> {
        val pageable = PageRequest.of(page, size,
            getSortOrder(sortOrder) as Sort.Direction,getSortAttribute(sortAttribute) , "alertId")
        if(filters.isEmpty()){
            return this.crud.findAll(pageable.sort).map {
                this.converter.toBoundary(it)
            }
                .log()
        }
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7) // Add 7 days to the current date
        var fromDate: Date = calendar.time
        var toDate : Date = calendar.time
        var keyword : String = "EOF"
        var location : String = "EOF"
        var website : String = "EOF"
        if(!filters["byTimestamp"].isNullOrEmpty()){
            val DATE_SHORT_LENGTH = 8
            var date = filters["byTimestamp"]!!
            var sdf:SimpleDateFormat = SimpleDateFormat("ddMMyyyy hh mm")
            if(date.length == DATE_SHORT_LENGTH){
                date += " 00 00"
            }
            var timestamp = sdf.parse(date)

            var minusH :Long = 0
            var minusD :Long = 0
            var plusD :Long = 0
            var plusH :Long = 0
            if(date.endsWith("00 00")){
                minusD = 1
                plusD = 1
            }else{
                plusH = 5.5.toLong()
                minusH = 5.5.toLong()
            }
            fromDate = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault())
                .minusHours(minusH)
                .minusDays(minusD)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
                .let { Date(it) }
            toDate = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault())
                .plusHours(plusH)
                .plusDays(plusD)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
                .let { Date(it) }
        }
        if(!filters["byKeywords"].isNullOrEmpty()) {
            keyword = filters["byKeywords"]!!
        }
        if(!filters["byLocation"].isNullOrEmpty()) {
            location = filters["byLocation"]!!
        }
        if(!filters["byWebsite"].isNullOrEmpty()) {
            website = filters["byWebsite"]!!
        }
        return this.crud.findAllByLocationOrWebsiteEndingWithIgnoreCaseOrTimestampBetweenOrKeywordsContainsIgnoreCase(
            location,
            website,
            fromDate,
            toDate,
            keyword,pageable)
            .map {
                this.converter.toBoundary(it)
            }
            .log()
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
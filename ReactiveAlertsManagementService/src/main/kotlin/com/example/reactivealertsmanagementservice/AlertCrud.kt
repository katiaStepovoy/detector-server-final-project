package com.example.reactivealertsmanagementservice

import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

interface AlertCrud : ReactiveMongoRepository<AlertEntity, String> {
    fun findAllByLocationOrWebsiteEndingWithIgnoreCaseOrTimestampBetweenOrKeywordsContainsIgnoreCase(
        @Param("location") location: String,
        @Param("website") website: String,
        @Param("fromDate") fromDate: Date,
        @Param("toDate") toDate: Date,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ):Flux<AlertEntity>
    fun findAllByLocation(
        @Param("location") location: String,
        pageable: Pageable
    ):Flux<AlertEntity>

    fun findAllByWebsite(
        @Param("website") website: String,
        pageable: Pageable
    ):Flux<AlertEntity>

    fun findAllByTimestamp(
        @Param("timestamp") timestamp: Date,
        pageable: Pageable
    ):Flux<AlertEntity>

    fun findAllByTimestampBetween(
        @Param("fromDate") fromDate: Date,
        @Param("toDate") toDate: Date,
        pageable: Pageable
    ):Flux<AlertEntity>

    fun findAllByKeywordsContains(
        @Param("keyword") keyword: String,
        pageable: Pageable
    ):Flux<AlertEntity>

    fun findByAlertIdNotNull(
        pageable: Pageable
    ): Flux<AlertEntity>


}

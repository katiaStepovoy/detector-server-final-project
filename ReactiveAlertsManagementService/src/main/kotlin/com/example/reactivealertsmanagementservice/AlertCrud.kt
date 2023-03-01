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

//    fun findAllByKeywordsContainsAndTimestampEqualsIgnoreCaseAndWebsite(
//        keyword: String?,
//        timestamp: String?,
//        website: String?,
//        location: String?,
//        pageable: Pageable
//    ): Flux<AlertEntity> {
//        val timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("ddMMyyyy[ HHmmss[.SSS]]"))
//            .atZone(ZoneId.systemDefault())
//            .toInstant()
//            .let { Date.from(it) }
//        val startTimestamp: Date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault())
//            .minusHours(1)
//            .toInstant(ZoneOffset.UTC)
//            .toEpochMilli()
//            .let { Date(it) }
//        val endTimestamp: Date = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault())
//            .plusHours(1)
//            .toInstant(ZoneOffset.UTC)
//            .toEpochMilli()
//            .let { Date(it) }
//        val exampleMatcher = ExampleMatcher.matchingAll()
//            .withIgnoreCase("timestamp")
//            .withMatcher("website", ExampleMatcher.GenericPropertyMatchers.ignoreCase())
//            .withMatcher("keywords", ExampleMatcher.GenericPropertyMatchers.contains())
//        val probe = AlertEntity()
//        probe.keywords = if (keyword != null) listOf(keyword) else null
//        probe.timestamp = timestamp
//        probe.website = if (website.isNullOrEmpty()) null else website
//        probe.location = if (location.isNullOrEmpty()) null else location
//        val example = Example.of(probe, exampleMatcher)
//        return this.findAll(example, pageable.sort).filter { it.timestamp!! in startTimestamp..endTimestamp }
//    }
}

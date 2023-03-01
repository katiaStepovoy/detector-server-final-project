package com.example.reactivealertsmanagementservice

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AlertService {
    fun create(alert: AlertBoundary): Mono<AlertBoundary>
    fun getSpecificAlert(alertId: String): Mono<AlertBoundary>
    fun cleanup(): Mono<Void>
//    fun search(
//        filterType: String,
//        filterValue: String,
//        sortAttribute: String,
//        sortOrder: String,
//        size: Int,
//        page: Int
//    ): Flux<AlertBoundary>
    fun search( filters: Map<String, String>, sortAttribute: String, sortOrder: String, size: Int, page: Int): Flux<AlertBoundary>
    fun updateAlert(alert: AlertBoundary): Mono<Void>

}

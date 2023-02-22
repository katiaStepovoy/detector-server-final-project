package com.example.reactivealertsmanagementservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class AlertsController(
    @Autowired val alertService: AlertService
    ){

    @RequestMapping(
        path = ["/alerts"],
        method = [RequestMethod.POST],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@RequestBody alert:AlertBoundary): Mono<AlertBoundary> {
        return this.alertService.create(alert)
    }
    @RequestMapping(
        path = ["/alert/{alertID}"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAlertById(@PathVariable alertID: String): Mono<AlertBoundary> {
        return this.alertService
            .getSpecificAlert(alertID)
    }

    @RequestMapping(
        path = ["/alerts"],
        method = [RequestMethod.DELETE]
    )
    fun deleteAllAlerts(): Mono<Void> {
        return this.alertService
            .cleanup()
    }

    @RequestMapping(
        path = ["/alert/{alertID}"],
        method = [RequestMethod.PUT],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateAlert(@PathVariable("alertID") alertID: String, @RequestBody alert: AlertBoundary): Mono<Void> {
        alert.alertId = alertID
        return alertService.updateAlert(alert)
    }

    @RequestMapping(
        path = ["/alerts"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun searchAlertByAttribute(
        @RequestParam(name = "filterType", required = false, defaultValue = "") filterType:String,
        @RequestParam(name = "filterValue", required = false, defaultValue = "") filterValue:String,
        @RequestParam(name = "sortBy", required = false, defaultValue = "alertId") sortAttribute:String,
        @RequestParam(name = "sortOrder", required = false, defaultValue = "ASC") sortOrder:String,
        @RequestParam(name = "page", required = false, defaultValue = "0") page:Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size:Int
    ): Flux<AlertBoundary> {
        return this.alertService
            .search(
                filterType,
                filterValue,
                sortAttribute,
                sortOrder,
                size,
                page
            )

    }


    /*
    *     @RequestMapping(
            path ="/alerts/history?filterType={criteriaType}&filterValue={criteriaValue}&sortBy={sortBy}&sortOrder={sortOrder}&page={page}&size={size}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AlertBoundary searchAlertsHistoryByAttribute(
            @RequestParam(name = "criteriaType", required = false, defaultValue = "") String criteriaType ,
            @RequestParam(name = "criteriaValue", required = false, defaultValue = "") String criteriaValue,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "email") String sortAttribute,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "ASC") String sortOrder
    ){
        return this.alertService.searchHistory(
                criteriaType,
                criteriaValue,
                size,
                page,
                sortAttribute,
                sortOrder
        );
    }*/
}
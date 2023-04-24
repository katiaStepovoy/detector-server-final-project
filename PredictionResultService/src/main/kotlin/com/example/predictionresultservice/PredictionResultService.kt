package com.example.predictionresultservice
import com.example.reactivealertsmanagementservice.AlertBoundary
import com.example.reactivewebcrawlerservice.ExtractedDataEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.json.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.ZoneId
import java.util.*

@Service
class PredictionResultService(
    @Autowired private val webClientBuilder: WebClient.Builder
) {
    private val clientCrawler = WebClient.create("http://localhost:8081")
    private val clientAlerts = WebClient.create("http://localhost:8082")
    private val clientModel = WebClient.create("http://127.0.0.1:8083")
    val mapper = jacksonObjectMapper()
    @Scheduled(fixedRate = 600000)
    fun processUnpredictedData(){
//        TODO: get all unpredicted extracted data from clientCrawler
        val unpredictedData = clientCrawler
            .get()
            .uri("/extracted")
            .retrieve()
            .bodyToFlux(object : ParameterizedTypeReference<ExtractedDataEntity>() {})
//        println("retrieved ExtractedDataEntity from crawler service")
//        TODO: for each unpredicted extracted data get predict from clientModel
        unpredictedData.flatMap { data ->
            val json = mapper.writeValueAsString(mapOf("text" to  data.content!!))
            println(json)
            WebClient.create()
                .post()
                .uri("http://127.0.0.1:8083/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})

                .flatMap { response ->
                    val predictedClass = response["predicted_class"] as? String
                    if (predictedClass == "related") {
                        // Create AlertBoundary object
                        val alert = AlertBoundary()
                        alert.website = data.websiteUrl
                        alert.location = data.location
                        alert.timestamp = Date.from(data.timestamp!!.atZone(ZoneId.systemDefault()).toInstant())
                        alert.content = data.content
                        alert.publisher = data.publisher
                        alert.keywords = response["keywords"] as? String

                        // Save AlertBoundary object
                        clientAlerts.post()
                            .uri("/alerts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(alert)
                            .retrieve()
                            .bodyToMono(AlertBoundary::class.java)
                            .map { savedAlert ->
                                println("Saved alert: $savedAlert")
                            }
                    }else {
                        Mono.empty<Unit>()
                    }
                }
                .then(clientCrawler.put()
                    .uri("/extracted/{extractedID}", data.extractedDataId)
                    .retrieve()
                    .bodyToMono(Void::class.java))
        }.subscribe()
    }

}
package com.example.predictionresultservice

import com.example.reactivealertsmanagementservice.AlertBoundary
import com.example.reactivewebcrawlerservice.ExtractedDataEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.ZoneId
import java.util.*

@Service
class PredictionResultService(
    @Autowired private val webClientBuilder: WebClient.Builder
) {
    private val clientCrawler = WebClient.create("http://localhost:8081")
    private val clientAlerts = WebClient.create("http://localhost:8082")
    private val clientModel = WebClient.create("http://localhost:8083")

    @Scheduled(fixedRate = 2000)
    fun processUnpredictedData(){
//        TODO: get all unpredicted extracted data from clientCrawler
        val unpredictedData = clientCrawler
            .get()
            .uri("/extracted")
            .retrieve()
            .bodyToFlux(object : ParameterizedTypeReference<ExtractedDataEntity>() {})
        println("retrieved ExtractedDataEntity from crawler service")
//        TODO: for each unpredicted extracted data get predict from clientModel
        unpredictedData.subscribe { data ->
            println(data.toString())
            val requestData = mutableMapOf<String, String>()
            requestData["text"] = data.content!!
            val response = clientModel.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestData)
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
                .block()
            println("Predicted class: ${response?.get("predicted_class")}, Probabilities: ${response?.get("probabilities")}")
            // Check if predicted_class is 'related'
            val predictedClass = response?.get("predicted_class") as? String
//        TODO: if predict result is 'related' post its alert to clientAlerts
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
                val savedAlert = clientAlerts.post()
                    .uri("/alerts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(alert)
                    .retrieve()
                    .bodyToMono(AlertBoundary::class.java)
                    .block()

                println("Saved alert: $savedAlert")
            }
        }
    }
}
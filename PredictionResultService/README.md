# Prediction Result Service 🧩
This is a microservice that processes unpredicted data extracted by a web crawler service and makes predictions using a machine learning model. If the predicted class is "related", it creates an alert and saves it to an alerts service.  
**It uses three external services: [Crawler Service 🕸️](../ReactiveWebCrawlerService/README.md), [Model Service 🧠 ](../TrainModelService/README.md), and [Alerts Service 🚨](../ReactiveAlertsManagementService/README.md).**
<div align="center">
    <div style="display: flex; flex-direction: row; justify-content: center; align-items: stretch;">
        <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
            <img src="https://drive.google.com/uc?export=§view&id=1FUY8lp1OvOW4j0G2ku7D2MhrEy7udV1H" style="margin-right: 10px;"/> 
            <p>This architecture describes services integration.</p>
        </div>
    </div>
</div>

## Requirements

    JDK 11 or higher
    Gradle
    Spring Boot
    Jackson JSON library
    WebClient library

## Installation

1. Clone the repository.
2. Build the project with gradle build.
3. Run the service with gradle bootRun.
4. You May need to run all the services mention before you run this service  

## Structure
The Prediction Result Service has the following directory structure:

    Prediction Result Service/
        build.gradle.kts
        ...
        src/
          main/
            kotlin/com/example/predictionresultservice/
            PredictionResultService.kt
            PredictionResultServiceApplication.kt 
          test/...
* `build.gradle.kts`: This file contains the Gradle configuration for the service.
* `PredictionResultService.kt`: This file contains the implementation of the Prediction Result Service.
* `PredictionResultServiceApplication.kt`: This file contains the Spring Boot application class for the service.

## Usage

* This service automatically runs every 10 minutes 
  `(fixedRate = 600000)` using a scheduler.
* The `processUnpredictedData()` function retrieves unpredicted extracted data from the Crawler Service.
* `For each` unpredicted extracted data, it gets a prediction from the Model Service.
* If the predicted class is `"related"`, the service creates an `AlertBoundary` object and saves it to the database using the Alerts Service.
* If the predicted class is `"not related"`, nothing is done.
* The unpredicted extracted data is then `marked as predicted` in the Crawler Service.
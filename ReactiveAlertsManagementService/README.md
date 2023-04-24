# Alerts Management Service 🚨

### General details:
Service's name: ReactiveAlertsManagementService
### Alerts
<div align="center">
    <img src="https://drive.google.com/uc?export=§view&id=1S-2jMdLZwR19ZQegfyNG_wa5BwZegPnr" width="350px" /> 
    <p>This API stands for alerts management.</p> 
    <a href="http://localhost:8082/webjars/swagger-ui/index.html">Alerts API</a>
</div>

#### Data Structure
* AlertBoundary:
```
        AlertBoundary{
            "alertId":"1a2bc3d4e5f6"
            "website":"https://some-social-media-site"
            "location":"lat lng"
            "timestamp": "2023-03-22T16:13:57"
            "feedback":"is_terrorism_related"
            "keywords":["massive", "attack"]
        }

```

### How do I get set up?

For application client [Detector_App](https://github.com/chenifargan/chenifargan_finalproject)  
For detect alerts on live  
[Reactive Web Crawler Service](../ReactiveWebCrawlerService/README.md) & [Prediction Result Service](../PredictionResultService/README.md)

clone this repository

* Note that this is subproject so make sure to do build for the project and then for the service you interest for (Alerts service)

* Before you running the service it is important that you will upload mongo image from docker or configure mongo atlas db alternatively, this subproject will create crawlersAndAlerts as database and ALERTS collection inside it.

* Important note: the consumer needs the producer to run in the background because it connects to it. That said, run the server locally and the app we refer you to.

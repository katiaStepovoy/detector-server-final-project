# Web Crawler Service 🕸️

### General details:
Service's name: ReactiveWebCrawlerService  
* This is a Scheduled Service, You can set your on delta time for the crawler go and scrape data from the urls.
### Extract data using web crawler
<div align="center">
    <div style="display: flex; flex-direction: row; justify-content: center; align-items: stretch;">
        <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
            <img src="https://drive.google.com/uc?export=§view&id=19J5sHrl9MYWoo9d_KIbQdwJ8cTD-tDbm" width="350px" height="242px" style="margin-right: 10px;"/> 
            <p>This DB is scraped data.</p>
        </div>
        <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
            <img src="https://drive.google.com/uc?export=§view&id=13QL5M-narJgKpEF8kM1u2qFX6fRmBcZs" width="350px" height="242px"/> 
            <p>This API stands for extracted data objects.</p> 
        </div>
    </div>
    <a href="http://localhost:8081/webjars/swagger-ui/index.html">Extracted Data API</a>
</div>

#### Data Structure
* ExtractedData:
```
        ExtractedData{
            "ExtractedDataId":"6411cf87ce11f237a0ad0a92"
            "publisher":"Tiffany Benitez"
            "content":"Searching for a reliable gun seller in our area"
            "websiteUrl":"https://some-social-media-site"
            "timestamp": "2023-03-22T16:13:57"
            "location":"lat lng"
        }

```

### How do I get set up?

[Social-Media-Simulations](https://github.com/katiaStepovoy/SocialMediaSimulations.git)

clone this repository

* Note that this is subproject so make sure to do build for the project and then for the service you interest for (Web crawler service)

* Before you running the service it is important that you will upload mongo image from docker or configure mongo atlas db alternatively, this subproject will create crawlersAndAlerts as database and EXTRACTED_DATA collection inside it.

* Important note: this service must run after Social-Media-Simulations is on air.
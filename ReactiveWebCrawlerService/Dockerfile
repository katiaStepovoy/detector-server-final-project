FROM openjdk:20-jdk
COPY build/libs/ReactiveWebCrawlerService-0.0.1-SNAPSHOT.jar crawler.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","crawler.jar"]
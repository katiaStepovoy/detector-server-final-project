FROM openjdk:20-jdk
COPY build/libs/PredictionResultService-0.0.1-SNAPSHOT.jar prediction.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","prediction.jar"]
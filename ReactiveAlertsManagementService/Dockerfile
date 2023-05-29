FROM openjdk:20-jdk
COPY build/libs/ReactiveAlertsManagementService-0.0.1-SNAPSHOT.jar alerts.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","alerts.jar"]
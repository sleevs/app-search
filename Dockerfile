
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 4567
ENTRYPOINT ["java", "-jar", "app.jar"]
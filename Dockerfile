# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src ./src

RUN chmod +x mvnw

# Stage 2: Run the app (slim runtime image)
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy only the built JAR from the build stage
COPY  target/campnest_backend-*.jar app.jar

# Expose the port your app uses
EXPOSE 8080

# Use ENTRYPOINT with prod profile so Spring Boot reads application-prod.properties
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

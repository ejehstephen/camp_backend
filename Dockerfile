# Use official Maven image to build the app
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Use a lightweight Java runtime for production
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the previous build stage
COPY --from=build /app/target/*.jar app.jar

# Railway assigns a dynamic port, use it
ENV PORT=8080
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]

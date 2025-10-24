FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app
COPY . .

WORKDIR /app/campnest_backend
RUN mvn clean package -DskipTests

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "target/campnest_backend-0.0.1-SNAPSHOT.jar"]

#!/bin/bash
chmod +x mvnw
./mvnw clean package -DskipTests
java -jar target/campnest_backend-0.0.1-SNAPSHOT.jar


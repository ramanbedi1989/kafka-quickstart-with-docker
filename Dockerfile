FROM maven:3.8.2-openjdk-11-slim as build
WORKDIR /build
COPY pom.xml /build/
COPY src /build/src/
RUN mvn clean package

FROM openjdk:11-jre-slim-buster
WORKDIR /app
COPY --from=build /build/target/kafka-quickstart-with-docker-1.0-SNAPSHOT-jar-with-dependencies.jar /app
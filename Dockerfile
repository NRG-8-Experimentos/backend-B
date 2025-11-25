FROM maven:3.9.9-eclipse-temurin-21-alpine as build

WORKDIR /opt/app/
COPY pom.xml .
RUN mvn dependency:go-offline

COPY ./src ./src
RUN mvn clean install -DskipTests

FROM openjdk:26-ea-21-slim

WORKDIR /opt/app/

COPY --from=build /opt/app/target/synhub-backend-0.0.1-SNAPSHOT.jar synhub-backend.jar

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "synhub-backend.jar"]

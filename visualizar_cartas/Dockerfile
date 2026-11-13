# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl wget \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --create-home --uid 10001 appuser
COPY --from=build /app/target/visualizar_cartas-1.0-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=mock
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Use a dedicated user instead of root for runtime hardening
RUN groupadd --system spring \
    && useradd --system --create-home --gid spring spring

# Expect the runnable jar to be built beforehand (e.g. via ./mvnw clean package)
ARG JAR_FILE=target/tournamentmgmt-*.jar
COPY ${JAR_FILE} app.jar

USER spring
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app/app.jar"]

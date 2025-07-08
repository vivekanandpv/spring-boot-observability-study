FROM openjdk:21-slim

ARG AGENT_JAR=opentelemetry-javaagent.jar
COPY ${AGENT_JAR} /app/opentelemetry-javaagent.jar

ARG APP_JAR=target/*.jar
COPY ${APP_JAR} /app/application.jar

WORKDIR /app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
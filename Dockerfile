#FROM postgres:16.3
#COPY init.sql /docker-entrypoint-initdb.d/
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/ascService-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]



# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл в контейнер
COPY target/ascService-0.0.1-SNAPSHOT.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM maven:3.8.6-amazoncorretto-17 AS build
#COPY pom.xml /build/
#WORKDIR /build/
#RUN mvn dependency:go-offline
#COPY src /build/src/
#RUN mvn package -DskipTests
##RUN mvn package
#
## Run state
#FROM openjdk:17-jdk-slim
#ARG JAR_FILE=/build/target/*.jar
#COPY --from=build $JAR_FILE /opt/ascService/app.jar
#ENTRYPOINT ["java", "-jar", "/opt/ascService/app.jar"]

#FROM maven:3.8.6-amazoncorretto-17
#WORKDIR /build/
#COPY pom.xml ./
#RUN mvn dependency:go-offline
#COPY src ./src/
#RUN mvn package -DskipTests
#RUN mkdir -p /opt/app/
#RUN cp $(find ./target/ -nane '*.jar') /opt/ascService/app.jar
#ENTRYPOINT ["java", "-jar", "/opt/ascService/app.jar"]
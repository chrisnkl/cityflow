FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

LABEL authors="chrisnkl"

COPY target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/assistencia-backend-1.0.0.jar app.jar
COPY python ./python

# Instalar Python para os scripts
RUN apk add --no-cache python3 py3-pip

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]

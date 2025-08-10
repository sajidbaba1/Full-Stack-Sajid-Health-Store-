# Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src/
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
VOLUME /tmp
EXPOSE 5454
ENTRYPOINT ["java", "-jar", "app.jar"]

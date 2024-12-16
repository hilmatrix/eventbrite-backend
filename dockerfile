# Stage 1: Build the application using Maven and JDK 21
FROM maven:3-eclipse-temurin-21 AS build

# Copy all files into the container
COPY . .

# Build the JAR file using Maven with the 'prod' profile and skip tests
RUN mvn clean package -Pprod -DskipTests

# Stage 2: Create a lightweight image for running the application
FROM eclipse-temurin:21-alpine

# Copy the JAR file from the build stage to the runtime container
COPY --from=build /target/eventbrite-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application runs on
EXPOSE 8080
EXPOSE 10000

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

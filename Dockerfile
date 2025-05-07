
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle
COPY src src
RUN chmod +x ./gradlew

# Build the JAR
RUN ./gradlew clean bootJar

# Stage 2: Run the application
FROM eclipse-temurin:23-jdk
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

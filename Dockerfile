# -------- Stage 1: Build the Spring Boot app using Gradle --------
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy only files needed for dependency resolution first for better caching
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true

# Now copy everything else and build
COPY . .
RUN gradle clean build --no-daemon

# -------- Stage 2: Use JDK 23 to run the app --------
FROM eclipse-temurin:23-jdk
WORKDIR /app

# Optional: Set environment variable (used by Spring Boot, for example)
ENV PORT=8090

# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the app port
EXPOSE 8090

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

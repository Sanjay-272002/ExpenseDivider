# Use a lightweight base image with JDK
FROM eclipse-temurin:23-jdk

# Set environment variables from .env later
ENV PORT=8090

# Set workdir
WORKDIR /appdocker build -t expense-backend .

# Copy jar file
COPY build/libs/app.jar app.jar

# Expose the port
EXPOSE 8090

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]

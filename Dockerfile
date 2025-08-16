# Use a standard, trusted base image with Java 17
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY src ./src

# Grant execution permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the fat JAR. This runs inside Render's servers.
RUN ./gradlew build -x test --no-daemon

# Expose the port Ktor will run on
EXPOSE 8080

# Command to run the application when the container starts
CMD ["java", "-jar", "build/libs/ktor-notes-backend-all.jar"]
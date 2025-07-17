# Use OpenJDK 17 slim base image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the project (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/snapshot-0.0.1-SNAPSHOT.jar"]
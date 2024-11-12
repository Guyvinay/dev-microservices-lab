# Use a base image with JDK 17
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file (replace 'your-application.jar' with your jar file's name)
COPY target/pros-revised-0.0.1-SNAPSHOT.jar pros-revised.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "pros-revised.jar"]

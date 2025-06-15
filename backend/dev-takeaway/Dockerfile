# Use a base image with JDK 17
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file (replace 'your-application.jar' with your jar file's name)
COPY target/dev-takeaway-1.0.1.jar dev-takeaway.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8888

# Run the jar file
ENTRYPOINT ["java", "-jar", "dev-takeaway.jar"]

# Use a base image with JDK 17
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the jar file (replace 'your-application.jar' with your jar file's name)
COPY target/dev-takeaway*.jar dev-takeaway.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8001

# Run the jar file
ENTRYPOINT ["java", "-jar", "dev-takeaway.jar"]

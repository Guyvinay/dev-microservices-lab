# Step 1: Use an official OpenJDK image as the base image
FROM eclipse-temurin:21-jdk-alpine

# Step 2: Set the working directory inside the container
# All subsequent commands will be executed in this directory
WORKDIR /app

# Step 3: Copy the packaged JAR file from the target folder to the container
# Replace `my-spring-app-1.0.jar` with your actual JAR file name
COPY target/dev-auth-1.0.4.jar /app/dev-auth.jar

# Step 4: Expose the application port (Spring Boot default port is 8080)
EXPOSE 8088

# Step 5: Define the command to run the Spring Boot application
# Runs the JAR file using Java
CMD ["java", "-jar", "/app/dev-auth.jar"]

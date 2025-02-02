# 1. Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# 2. Set working directory inside the container
WORKDIR /app

# 3. Copy the built JAR file into the container
COPY target/dev-auth.jar /app/dev-auth.jar

# 4. Expose the application port
EXPOSE 8080

# 5. Define the startup command
CMD ["java", "-jar", "/app/dev-auth.jar"]

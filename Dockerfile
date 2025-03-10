# Base image
FROM openjdk:17-jdk

# Maintainer information
LABEL maintainer="ze2@kakao.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8443 available to the world outside this container
EXPOSE 8443

# The application's jar file
ARG JAR_FILE=build/libs/exit-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Create a directory for logs
RUN mkdir -p /app

# Run the jar file
ENTRYPOINT ["sh", "-c", "java -jar app.jar > /app/log.txt 2>&1"]

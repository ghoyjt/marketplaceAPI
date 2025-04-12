# Build stage
FROM gradle:8.10.1-jdk17 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean build --no-daemon

# Run stage
FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
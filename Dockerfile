# Stage 1: Build the application
FROM gradle:8.3.0-jdk17 as build

WORKDIR /home/gradle/project

COPY . .

RUN gradle wrapper

RUN ./gradlew build -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

WORKDIR /opt/nimble

COPY --from=build /home/gradle/project/build/libs/server_spring-0.0.1.jar .

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "server_spring-0.0.1.jar"]

FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar app.jar"]

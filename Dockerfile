
FROM gradle:9.7.1-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
USER gradle
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

```dockerfile
FROM gradle:9.7.1-jdk21 AS build

WORKDIR /app

ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY --chown=gradle:gradle . .

RUN mkdir -p /home/gradle/.gradle \
    && chown -R gradle:gradle /home/gradle/.gradle

USER gradle

RUN gradle --gradle-user-home /home/gradle/.gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
```

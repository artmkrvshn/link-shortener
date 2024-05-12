FROM eclipse-temurin:21-jdk-alpine as BUILDER
WORKDIR /link-shortener
COPY gradle ./gradle
COPY src ./src
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon -i clean bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /link-shortener
COPY --from=BUILDER /link-shortener/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
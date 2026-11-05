FROM node:22-bookworm-slim AS frontend-build
WORKDIR /frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
ARG VITE_TOSS_CLIENT_KEY
ARG VITE_HOME_HIDDEN_EXPERT_ID
RUN npm run build

# BlogRest와 같이 Java 25로 빌드하고 실행에는 JRE만 사용합니다.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src/main src/main
COPY --from=frontend-build /frontend/dist/ src/main/resources/static/
# 테스트는 이미지 빌드 전에 별도로 실행합니다.
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=Asia/Seoul \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=40.0 -XX:+UseSerialGC -XX:ActiveProcessorCount=2"
COPY --from=build --chown=1000:1000 /app/build/libs/efy-0.0.1-SNAPSHOT.jar app.jar
USER 1000:1000
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

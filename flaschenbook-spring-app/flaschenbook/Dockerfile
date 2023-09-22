# Gradle 이미지를 기반으로 빌드 환경 생성
FROM gradle:7.2.0-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 빌드에 필요한 파일들 복사
COPY build.gradle settings.gradle ./
COPY gradlew gradlew
COPY gradle gradle
COPY src src/

# 프로젝트 빌드
RUN ./gradlew build -x test

# OpenJDK 이미지를 기반으로 애플리케이션 실행 환경 생성
FROM openjdk:17

# 배포 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar /app/spring-boot-application.jar

# 포트 노출
EXPOSE 8000

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/spring-boot-application.jar"]

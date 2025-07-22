# Dockerfile (backend 폴더 안에 위치)
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 포트 노출 (스프링 부트 기본 포트)
EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]

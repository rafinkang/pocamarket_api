# 심플한 Spring Boot 프로덕션 Dockerfile
FROM openjdk:17-alpine
WORKDIR /src

# 전체 프로젝트 복사
COPY . .

# 실행 권한 부여
RUN chmod +x gradlew

# 환경 설정
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8080

# Gradle로 직접 실행 (가장 안정적)
CMD ["./gradlew", "bootRun", "--no-daemon"]
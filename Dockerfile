# ================= STAGE 1: 빌드 스테이지 (Builder) =================
# 빌드를 위해 JDK(Java Development Kit)가 포함된 이미지를 사용하고 'builder'라는 별명을 붙입니다.
FROM openjdk:17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일들을 먼저 복사하여 Docker 레이어 캐시를 활용합니다.
# 이 파일들이 변경되지 않으면 다음 RUN 단계는 캐시된 결과를 사용합니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 전체 복사
# 소스 코드만 변경되었을 경우, 위 단계들은 캐시 처리되어 빌드 속도가 향상됩니다.
COPY src ./src

# 실행 권한 부여
RUN chmod +x ./gradlew

# Gradle을 사용하여 실행 가능한 JAR 파일을 빌드합니다.
# --no-daemon 옵션으로 빌드 후 Gradle 프로세스가 남지 않도록 합니다.
RUN ./gradlew bootJar --no-daemon


# ================= STAGE 2: 실행 스테이지 (Runner) =================
# 최종 실행 환경은 JDK가 아닌 JRE(Java Runtime Environment)만 포함된 훨씬 가벼운 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 보안 강화를 위해 root가 아닌 별도의 사용자를 생성하여 실행합니다.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드 스테이지('builder')에서 생성된 JAR 파일만 복사해옵니다.
# build/libs/*.jar 패턴으로 실제 생성된 jar 파일 이름을 찾아 복사하고, 이름을 app.jar로 통일합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 8080 포트 노출
EXPOSE 8080

# 최종 실행 명령어: java 명령어로 JAR 파일을 직접 실행합니다.
# JAVA_OPTS 환경 변수를 참조하여 JVM 메모리 설정을 적용합니다.
CMD ["java", "-jar", "app.jar"]
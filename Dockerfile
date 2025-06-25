FROM openjdk:17-alpine AS builder
WORKDIR /src

COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM openjdk:17-alpine
WORKDIR /src

COPY --from=builder /src/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx256m -Xms128m"

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
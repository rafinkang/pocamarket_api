FROM openjdk:17-alpine
WORKDIR /src

COPY . .
RUN ./gradlew build --no-daemon

EXPOSE 8080
CMD ["./gradlew", "bootRun"]
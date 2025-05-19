FROM openjdk:17-alpine
WORKDIR /src

COPY . .

RUN chmod +x gradlew

EXPOSE 8080
CMD ["./gradlew", "bootRun", "--no-daemon"]
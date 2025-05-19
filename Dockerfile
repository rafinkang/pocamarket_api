FROM openjdk:17-jdk-slim
WORKDIR /src
COPY build/libs/*.jar pocamarket.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pocamarket.jar"]
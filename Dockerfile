FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} readinglog.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/readinglog.jar"]
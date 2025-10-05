FROM openjdk:21-jdk

WORKDIR /app

ARG FILE_JAR=target/*.jar
COPY ${FILE_JAR} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
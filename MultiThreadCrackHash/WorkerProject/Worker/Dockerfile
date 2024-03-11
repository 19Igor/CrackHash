FROM openjdk:18
ARG JAR_FILE=target/*.jar
COPY ./target/Worker-0.0.1.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
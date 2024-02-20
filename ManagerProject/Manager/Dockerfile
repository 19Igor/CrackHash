FROM openjdk:18
ARG JAR_FILE=target/*.jar
COPY ./target/Manager-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

# docker build -t spring-manager .
# docker run spring-manager  здесь можно ещё порты прокинуть


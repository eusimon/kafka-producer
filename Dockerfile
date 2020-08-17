FROM openjdk:8-jre-alpine

MAINTAINER goodzollder@gmail.com

EXPOSE 8080

# Add application jsr to the container:
ARG JAR_FILE=target/producer-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} producer.jar

# run jar file
ENTRYPOINT ["java","-jar","/producer.jar"]
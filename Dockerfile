FROM openjdk:17-slim

VOLUME /mnt/service
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
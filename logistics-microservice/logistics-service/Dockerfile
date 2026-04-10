FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/logistics-service-1.0.0.jar logistics-service.jar
ENTRYPOINT ["java","-jar","/logistics-service.jar"]

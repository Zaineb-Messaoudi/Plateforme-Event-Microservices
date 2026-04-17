FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/config-server-1.0.0.jar config-server.jar
ENTRYPOINT ["java","-jar","/config-server.jar"]

FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/eureka-server-1.0.0.jar eureka-server.jar
ENTRYPOINT ["java","-jar","/eureka-server.jar"]

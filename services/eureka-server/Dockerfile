FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY target/eureka-server-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

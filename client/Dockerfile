FROM eclipse-temurin:17.0.2_8-jre-alpine
LABEL authors="Rafael Fernandes Pereira"

ENV SERVER_PORT=8080

RUN mkdir /opt/app
EXPOSE 8080
EXPOSE 8081

COPY target/restaurant-0.0.1-SNAPSHOT.jar /opt/app/japp.jar

CMD ["java", "-jar", "/opt/app/japp.jar", "--server.port=${SERVER_PORT}"]


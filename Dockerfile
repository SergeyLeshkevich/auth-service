FROM openjdk:17-alpine

ADD /build/libs/auth-service-0.0.1-SNAPSHOT.jar /app/

CMD ["java", "-Xmx200m", "-jar", "/app/auth-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 9090
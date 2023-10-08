FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/oda-config-service-0.1.jar /app

CMD ["java","--add-opens","java.base/java.time=ALL-UNNAMED","-jar","oda-config-service-0.1.jar"]

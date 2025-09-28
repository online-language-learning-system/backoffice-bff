FROM openjdk:17
COPY target/backoffice-bff*.jar backoffice-bff.jar
ENTRYPOINT ["java", "-jar", "/backoffice-bff.jar"]
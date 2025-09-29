FROM bellsoft/liberica-runtime-container:jdk-25-glibc
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

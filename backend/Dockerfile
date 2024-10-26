# Stage 1: Build the application
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Generate keystore and run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Generate keystore.p12
RUN keytool -genkeypair -alias bwp -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass 123456 -dname "CN=example.com, OU=IT, O=Example, L=City, S=State, C=US"

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
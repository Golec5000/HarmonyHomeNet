# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /backend_app

# Copy the pom.xml file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-alpine

# Install openssl
RUN apk add --no-cache openssl

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /backend_app/target/*.jar app.jar

# Generate RSA key pair
RUN openssl genrsa -out keypair.pem 2048

# Extract the public key
RUN openssl rsa -in keypair.pem -pubout -out publicKey.pem

# Convert the private key to PKCS#8 format
RUN openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out privateKey.pem

# Generate a self-signed certificate and keystore.p12 with the corrected password
RUN keytool -genkeypair \
    -alias myalias \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore keystore.p12 \
    -validity 365 \
    -storepass haslo123 \
    -keypass haslo123 \
    -dname "CN=localhost"

# Expose port 8443
EXPOSE 8443

# Run the application with SSL settings and the corrected password
ENTRYPOINT ["java", "-jar", "app.jar", \
    "--server.port=8443", \
    "--server.ssl.key-store=/app/keystore.p12", \
    "--server.ssl.key-store-password=haslo123", \
    "--server.ssl.key-store-type=PKCS12", \
    "--server.ssl.key-alias=myalias", \
    "--spring.mail.host=smtp.gmail.com", \
    "--spring.mail.port=587", \
    "--spring.mail.username=your_email", \
    "--spring.mail.password=your_password", \
    "--spring.mail.properties.mail.smtp.auth=true", \
    "--spring.mail.properties.mail.smtp.starttls.enable=true"]
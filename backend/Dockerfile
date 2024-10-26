# Etap 1: Budowanie aplikacji
FROM maven:3.8.5-openjdk-17 AS build

# Ustawienie katalogu roboczego
WORKDIR /app

# Kopiowanie pliku pom.xml i pobieranie zależności
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiowanie kodu źródłowego
COPY src ./src

# Budowanie aplikacji
RUN mvn package -DskipTests

# Etap 2: Uruchamianie aplikacji
FROM openjdk:17-jdk-alpine

# Ustawienie katalogu roboczego
WORKDIR /app

# Kopiowanie zbudowanego pliku JAR z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

# Generowanie samopodpisanego certyfikatu i keystore.p12 z poprawionym hasłem
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

# Eksponowanie portu 8443
EXPOSE 8443

# Uruchamianie aplikacji z ustawieniami SSL i poprawionym hasłem
ENTRYPOINT ["java", "-jar", "app.jar", \
    "--server.port=8443", \
    "--server.ssl.key-store=/app/keystore.p12", \
    "--server.ssl.key-store-password=haslo123", \
    "--server.ssl.key-store-type=PKCS12", \
    "--server.ssl.key-alias=myalias"]

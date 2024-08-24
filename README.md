# HarmonyHomeNet

## Description
HarmonyHomeNet is a software tool designed to support the management of residents and property owners. It provides functionalities to handle various aspects of property management, including building information, resident details, and forum discussions.

## Technologies Used

- Java (version 17 or higher)
- Spring Boot 3
- Hibernate
- JPA
- Lombok
- PostgreSQL
- Docker
- Maven
- IntelliJ IDEA 2024.2
- TLS/SSL for secure HTTPS connections

## Setup .env file and set up keys

Before running the application, you need to create a `.env` file in the resources directory of the project.
To do this use `.env.example` as a template and chane file to `.env`.

To generate the keys for the `.env` file, you can use the following commands:

```shell
keytool -genkeypair -alias myalias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
```


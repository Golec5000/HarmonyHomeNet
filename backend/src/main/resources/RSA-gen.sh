#!/bin/bash

# Generate RSA key pair
openssl genrsa -out keypair.pem 2048

# Extract the public key
openssl rsa -in keypair.pem -pubout -out publicKey.pem

# Convert the private key to PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out privateKey.pem

# Create a PKCS12 keystore using keytool
keytool -genkeypair -alias bwp -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass 123456 -dname "CN=tak, OU=tak, O=tak, L=tak, ST=tak, C=US"

# Clean up the original key pair file
rm keypair.pem

echo "RSA keys and keystore generated: publicKey.pem, privateKey.pem, and keystore.p12"
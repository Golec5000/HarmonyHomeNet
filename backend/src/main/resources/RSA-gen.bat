@echo off

REM Generowanie pary kluczy RSA
openssl genrsa -out keypair.pem 2048

REM Wyodrębnienie klucza publicznego
openssl rsa -in keypair.pem -pubout -out publicKey.pem

REM Konwersja klucza prywatnego do formatu PKCS#8
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out privateKey.pem

REM Czyszczenie oryginalnego pliku z parą kluczy
del keypair.pem

echo RSA keys and keystore generated: publicKey.pem, privateKey.pem, and keystore.p12
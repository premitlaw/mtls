# sslUtil
java util for mtls setup

# gen keypair priv-pub rsa2048 for server and client respectively
keytool -genkey -alias server -keyalg RSA -keypass password -storepass password -keystore server.jks
keytool -genkey -alias client -keyalg RSA -keypass password -storepass password -keystore client.jks

 # export public keys from keystores for server/client respectively
keytool -exportcert -keystore C:/sslCerts/client/client.jks -storepass password -alias client -rfc -file client-public.cer

 # import public keys to truststores for server/client respectively
keytool -keystore C:/sslCerts/server/truststore.jks -importcert -file C:/sslCerts/server/client-public.cer -alias client -storepass password

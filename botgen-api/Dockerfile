FROM parrotstream/ubuntu-java:xenial-8

ARG KEYSTORE_PASS
ARG KEY_PASS
ARG XMX

COPY target/scala-2.13/botgen-api-assembly-1.0-SNAPSHOT.jar .
COPY cert/server.p12 server.p12
RUN echo $KEY_PASS | keytool -importkeystore -srckeystore server.p12 -storepass $KEYSTORE_PASS -keystore keystore -srcstoretype pkcs12 -alias shared
EXPOSE 443
CMD java -jar botgen-api-assembly-1.0-SNAPSHOT.jar
FROM alpine:3.11
MAINTAINER koszegy@inqool.cz

COPY target/vzb-1.0-SNAPSHOT.jar /usr/src/api.jar
COPY run.sh /run.sh

RUN chmod +x /run.sh && \
    apk add --update \
      ca-certificates ca-certificates-cacert \
      openjdk11-jre openjdk11-jre-headless openssl && \
    rm -rf /var/cache/apk/*

ENTRYPOINT ["/run.sh"]

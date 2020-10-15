FROM java:8-jre-alpine
MAINTAINER koszegy@inqool.cz

COPY target/vzb-1.0-SNAPSHOT.jar /usr/src/api.jar
COPY run.sh /run.sh

RUN chmod +x /run.sh

ENTRYPOINT ["/run.sh"]

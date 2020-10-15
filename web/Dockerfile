FROM alpine:3.9
MAINTAINER koszegy@inqool.cz

RUN apk add --update apache2 apache2-proxy openssl \
    && rm -rf /var/cache/apk/* && \
    mkdir -p /run/apache2 && \
    rm -rf /var/www/localhost/htdocs

COPY build /var/www/localhost/htdocs
COPY httpd.conf /etc/apache2/httpd.conf

ADD run.sh /run.sh
RUN chmod +x /run.sh

RUN sed -i 's/^LoadModule lbmethod_/#&/' /etc/apache2/conf.d/proxy.conf

ENTRYPOINT ["/run.sh"]

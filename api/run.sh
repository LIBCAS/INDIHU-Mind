#!/bin/sh

cd /usr/src

if [ "$SPRING_PROFILE" == "NO_SPRING_PROFILE" ]; then
    java -jar -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 api.jar > api.log
else
    java -jar -Xmx1024m -Dspring.profiles.active=$SPRING_PROFILE -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 api.jar > api.log
fi;

tail -f /etc/issue

FROM maven:alpine
ARG NAME
ADD $NAME app.jar
ENTRYPOINT exec java $JAVA_OPTIONS -jar /app.jar

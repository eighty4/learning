FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y netcat && apt-get clean

HEALTHCHECK --start-period=1s --interval=3s CMD nc -z localhost 8070

COPY ./akimbo-test-app.jar /akimbo-test-app.jar

EXPOSE 8070

CMD java -jar /akimbo-test-app.jar

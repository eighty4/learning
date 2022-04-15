# Spring Native Webapp

Using Spring Native to build a bare-metal executable to run Spring Boot in a GraalVM native image

- Run app as Java process
```
./gradlew bootRun
```

- Build container from GraalVM native image and run on Docker
```
./gradlew bootBuildImage
docker run --rm -it -p 8080:8080 spring-native-webapp:0.0.1-SNAPSHOT spring-native-webapp
```

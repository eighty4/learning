# GraalVM Script Engine

Using GraalVM to execute JS from a Java process compiled to native byte code

```
./gradlew nativeBuild
./build/native/nativeBuild/script-engine-fun
```

📌 Dynamically generating JS modules has a pretty weak extension point from GraalVM's interfaces requiring extra deps on `java.nio` and added boilerplate of a hundred extra LoC

📌 Authoring an API as a dynamically generated JS module prevents IDE support for the API

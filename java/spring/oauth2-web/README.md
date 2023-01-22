# Spring OAuth2 Web Security

Update `src/main/resources/application.properties` with an OAuth2 client id and secret, and replace (or keep) `github`
with the OAuth2 provider name (`facebook`, `google`, `apple`, etc.).

See https://spring.io/guides/tutorials/spring-boot-oauth2 for a complete example.

`
./gradlew bootRun
open http://localhost:8080/secured
`

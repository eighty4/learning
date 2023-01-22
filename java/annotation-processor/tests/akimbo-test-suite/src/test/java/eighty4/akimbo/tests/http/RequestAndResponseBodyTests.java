package eighty4.akimbo.tests.http;

import com.google.gson.Gson;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestAndResponseBodyTests {

    private HttpClient httpClient = HttpClient.newBuilder().build();

    @Test
    public void jsonRequestBodyAndVoidReturnType() throws Exception {
        String requestBody = new Gson().toJson(new RequestAndResponseBodyScenarios.SomeData());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8070/payloads/jsonRequestBodyAndVoidReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("");
    }

    @Test
    public void noRequestBodyAndVoidReturnType() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8070/payloads/noRequestBodyAndVoidReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("");
    }

    @Test
    public void jsonRequestBodyAndValueReturnType() throws Exception {
        String requestBody = new Gson().toJson(new RequestAndResponseBodyScenarios.SomeData());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8070/payloads/jsonRequestBodyAndValueReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("{\"data\":\"foobar\"}");
    }

    @Test
    public void noRequestBodyAndValueReturnType() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8070/payloads/noRequestBodyAndValueReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("{\"data\":\"foobar\"}");
    }

    @Test
    public void jsonRequestBodyAndMonoValueReturnType() throws Exception {
        String requestBody = new Gson().toJson(new RequestAndResponseBodyScenarios.SomeData());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8070/payloads/jsonRequestBodyAndMonoValueReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("{\"data\":\"foobar\"}");
    }

    @Test
    public void noRequestBodyAndMonoValueReturnType() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8070/payloads/noRequestBodyAndMonoValueReturnType"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("{\"data\":\"foobar\"}");
    }
}

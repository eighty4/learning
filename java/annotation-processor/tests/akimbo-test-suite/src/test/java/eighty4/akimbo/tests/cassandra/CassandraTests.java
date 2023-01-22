package eighty4.akimbo.tests.cassandra;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class CassandraTests {

    private HttpClient httpClient = HttpClient.newBuilder().build();

    @Test
    public void jsonRequestBodyAndVoidReturnType() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8070/cassandra/dao/component"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("column data");
    }
}

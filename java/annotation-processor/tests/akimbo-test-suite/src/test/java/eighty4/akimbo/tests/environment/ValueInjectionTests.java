package eighty4.akimbo.tests.environment;

import com.google.gson.Gson;
import eighty4.akimbo.tests.environment.ValueInjectionScenarios.ValueData;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueInjectionTests {

    private HttpClient httpClient = HttpClient.newBuilder().build();
    private Gson gson = new Gson();

    private void propertyValueTest(String path, ValueData<?> expectedValue) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8070/values/" + path))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo(gson.toJson(expectedValue));
    }

    @Test
    public void stringValue() throws Exception {
        propertyValueTest("string", new ValueData<>("foobar"));
    }

    @Test
    public void integerValue() throws Exception {
        propertyValueTest("integer", new ValueData<>(42));
    }

    @Test
    public void shortValue() throws Exception {
        propertyValueTest("short", new ValueData<>(3276));
    }

    @Test
    public void longValue() throws Exception {
        propertyValueTest("long", new ValueData<>(21474836470L));
    }

    @Test
    public void doubleValue() throws Exception {
        propertyValueTest("double", new ValueData<>(4.2D));
    }

    @Test
    public void floatValue() throws Exception {
        propertyValueTest("float", new ValueData<>(3.4028235E38F));
    }

    @Test
    public void bigIntegerValue() throws Exception {
        propertyValueTest("bigInteger", new ValueData<>(new BigInteger("922337203685477580700")));
    }

    @Test
    public void bigDecimalValue() throws Exception {
        propertyValueTest("bigDecimal", new ValueData<>(new BigDecimal("9223372036854775807.1")));
    }

    @Test
    public void booleanTrueValue() throws Exception {
        propertyValueTest("booleanTrue", new ValueData<>(true));
    }

    @Test
    public void booleanOneValue() throws Exception {
        propertyValueTest("booleanOne", new ValueData<>(true));
    }

    @Test
    public void booleanYesValue() throws Exception {
        propertyValueTest("booleanYes", new ValueData<>(true));
    }

    @Test
    public void booleanFalseValue() throws Exception {
        propertyValueTest("booleanFalse", new ValueData<>(false));
    }

    @Test
    public void booleanZeroValue() throws Exception {
        propertyValueTest("booleanZero", new ValueData<>(false));
    }

    @Test
    public void booleanNoValue() throws Exception {
        propertyValueTest("booleanNo", new ValueData<>(false));
    }

    @Test
    public void uuidValue() throws Exception {
        propertyValueTest("uuid", new ValueData<>(UUID.fromString("f96a7c85-59c2-4f5c-95f6-5995cb25cd05")));
    }
}

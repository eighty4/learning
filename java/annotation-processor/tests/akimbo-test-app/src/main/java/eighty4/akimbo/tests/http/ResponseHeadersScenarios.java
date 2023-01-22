package eighty4.akimbo.tests.http;

import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestBody;
import eighty4.akimbo.annotation.http.RequestMapping;
import eighty4.akimbo.http.HttpMethod;
import eighty4.akimbo.http.ResponseHeaders;
import reactor.core.publisher.Mono;

@HttpService
@RequestMapping("/headers")
public class ResponseHeadersScenarios {

    public static class SomeData {
        private String data = "foobar";

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    @RequestMapping(path = "/jsonRequestBodyAndVoidReturnType", method = HttpMethod.POST)
    public void jsonRequestBodyAndVoidReturnType(@RequestBody SomeData data, ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
    }

    @RequestMapping(path = "/noRequestBodyAndVoidReturnType", method = HttpMethod.POST)
    public void noRequestBodyAndVoidReturnType(ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
    }

    @RequestMapping(path = "/jsonRequestBodyAndValueReturnType", method = HttpMethod.POST)
    public SomeData jsonRequestBodyAndValueReturnType(@RequestBody SomeData data, ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
        return new SomeData();
    }

    @RequestMapping(path = "/noRequestBodyAndValueReturnType", method = HttpMethod.POST)
    public SomeData noRequestBodyAndValueReturnType(ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
        return new SomeData();
    }

    @RequestMapping(path = "/jsonRequestBodyAndMonoValueReturnType", method = HttpMethod.POST)
    public Mono<SomeData> jsonRequestBodyAndMonoValueReturnType(@RequestBody SomeData data, ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
        return Mono.just(new SomeData());
    }

    @RequestMapping(path = "/noRequestBodyAndMonoValueReturnType", method = HttpMethod.POST)
    public Mono<SomeData> noRequestBodyAndMonoValueReturnType(ResponseHeaders responseHeaders) {
        responseHeaders.add("x-response-header", "added to request");
        return Mono.just(new SomeData());
    }
}

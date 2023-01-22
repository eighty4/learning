package eighty4.akimbo.tests.http;

import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestBody;
import eighty4.akimbo.annotation.http.RequestMapping;
import eighty4.akimbo.http.HttpMethod;
import reactor.core.publisher.Mono;

@HttpService
@RequestMapping("/payloads")
public class RequestAndResponseBodyScenarios {

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
    public void jsonRequestBodyAndVoidReturnType(@RequestBody SomeData data) {

    }

    @RequestMapping(path = "/noRequestBodyAndVoidReturnType", method = HttpMethod.POST)
    public void noRequestBodyAndVoidReturnType() {

    }

    @RequestMapping(path = "/jsonRequestBodyAndValueReturnType", method = HttpMethod.POST)
    public SomeData jsonRequestBodyAndValueReturnType(@RequestBody SomeData data) {
        return new SomeData();
    }

    @RequestMapping(path = "/noRequestBodyAndValueReturnType", method = HttpMethod.POST)
    public SomeData noRequestBodyAndValueReturnType() {
        return new SomeData();
    }

    @RequestMapping(path = "/jsonRequestBodyAndMonoValueReturnType", method = HttpMethod.POST)
    public Mono<SomeData> jsonRequestBodyAndMonoValueReturnType(@RequestBody SomeData data) {
        return Mono.just(new SomeData());
    }

    @RequestMapping(path = "/noRequestBodyAndMonoValueReturnType", method = HttpMethod.POST)
    public Mono<SomeData> noRequestBodyAndMonoValueReturnType() {
        return Mono.just(new SomeData());
    }
}

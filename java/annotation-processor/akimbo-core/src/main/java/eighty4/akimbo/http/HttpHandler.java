package eighty4.akimbo.http;

import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

public abstract class HttpHandler<T> implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
    private final HttpMethod httpMethod;
    private final String path;
    private final T component;

    public HttpHandler(HttpMethod httpMethod, String path, T component) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.component = component;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public T getComponent() {
        return component;
    }
}

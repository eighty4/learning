package eighty4.akimbo.http;

import eighty4.akimbo.annotation.Value;

import javax.inject.Provider;
import java.util.List;
import java.util.Optional;

public class HttpServiceProvider implements Provider<Optional<HttpServiceWrapper>> {

    private final int httpPort;
    private final List<HttpHandler<?>> httpHandlers;

    public HttpServiceProvider(@Value("akimbo.http.port:8080") int httpPort, List<HttpHandler<?>> httpHandlers) {
        this.httpPort = httpPort;
        this.httpHandlers = httpHandlers;
    }

    @Override
    public Optional<HttpServiceWrapper> get() {
        return httpHandlers.isEmpty() ? Optional.empty() : Optional.of(new HttpServiceWrapper(httpPort, httpHandlers));
    }
}

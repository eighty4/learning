package eighty4.akimbo.http;

import com.google.common.flogger.FluentLogger;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;

import java.util.List;
import java.util.function.BiFunction;

public class HttpServiceWrapper {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private interface HttpHandlerRegistrant extends BiFunction<String, HttpHandler<?>, HttpServerRoutes> {
    }

    private final int httpPort;
    private final List<HttpHandler<?>> httpMappings;

    HttpServiceWrapper(int httpPort, List<HttpHandler<?>> httpMappings) {
        this.httpPort = httpPort;
        this.httpMappings = httpMappings;
    }

    public void start() {
        logger.atInfo().log("Starting http server on port %d", httpPort);
        long now = System.currentTimeMillis();
        Flux.fromIterable(httpMappings)
                .collectList()
                .flatMap(httpHandlers -> {
                    DisposableServer server = HttpServer.create()
                            .port(httpPort)
                            .route(routes -> httpHandlers.forEach(httpMapping -> registerHttpHandler(routes, httpMapping)))
                            .bindNow();
                    logger.atInfo().log("starting http server took %dms", System.currentTimeMillis() - now);
                    return server.onDispose();
                })
                .block();
    }

    private void registerHttpHandler(HttpServerRoutes routes, HttpHandler<?> httpMapping) {
        getHttpHandlerRegistrant(routes, httpMapping).apply(httpMapping.getPath(), httpMapping);
    }

    private HttpHandlerRegistrant getHttpHandlerRegistrant(HttpServerRoutes routes, HttpHandler<?> httpMapping) {
        if (httpMapping.getHttpMethod() == null) {
            return routes::get;
        }
        switch (httpMapping.getHttpMethod()) {
            case GET:
                return routes::get;
            case POST:
                return routes::post;
            case DELETE:
                return routes::delete;
            case PUT:
                return routes::put;
            case OPTIONS:
                return routes::options;
            case HEAD:
                return routes::head;
            default:
                throw new IllegalStateException("?");
        }
    }
}

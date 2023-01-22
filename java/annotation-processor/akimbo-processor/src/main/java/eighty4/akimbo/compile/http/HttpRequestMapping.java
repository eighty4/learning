package eighty4.akimbo.compile.http;

import eighty4.akimbo.annotation.http.RequestMapping;
import eighty4.akimbo.http.HttpMethod;

import javax.lang.model.element.Element;
import java.util.Optional;

import static eighty4.akimbo.compile.util.AkimboElementUtils.getAnnotationValue;

class HttpRequestMapping {
    private final HttpMethod method;
    private final String path;

    HttpRequestMapping(Element handlerMethod) {
        this(handlerMethod, null);
    }

    HttpRequestMapping(Element handlerMethod, HttpRequestMapping parent) {
        this.method = getAnnotationValue(handlerMethod, RequestMapping.class, String.class, "method")
                .map(HttpMethod::valueOf)
                .orElseGet(() -> Optional.ofNullable(parent).map(HttpRequestMapping::getMethod).orElse(HttpMethod.GET));

        Optional<String> componentPath = Optional.ofNullable(parent)
                .map(HttpRequestMapping::getPath)
                .map(cp -> !cp.startsWith("/") ? "/" + cp : cp)
                .map(cp -> cp.endsWith("/") ? cp.substring(0, cp.length() - 2) : cp);
        Optional<String> handlerPath = getAnnotationValue(handlerMethod, RequestMapping.class, String.class, "path", "value")
                .map(hp -> hp.startsWith("/") ? hp.substring(1) : hp);

        this.path = componentPath.map(cp -> handlerPath.map(hp -> cp + "/" + hp).orElse(cp))
                .orElseGet(() -> handlerPath.map(hp -> "/" + hp).orElse(null));

        if (parent != null && path == null) {
            throw new IllegalStateException("no request path configured for " + handlerMethod.getSimpleName().toString());
        }
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }
}
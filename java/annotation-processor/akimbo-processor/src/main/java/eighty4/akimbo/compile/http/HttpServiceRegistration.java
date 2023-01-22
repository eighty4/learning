package eighty4.akimbo.compile.http;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.annotation.http.RequestMapping;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.service.ServiceRegistration;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

import static eighty4.akimbo.compile.util.AkimboElementUtils.hasAnnotation;

public class HttpServiceRegistration extends ServiceRegistration<HttpHandlerRegistration> {
    private final String name;
    private final HttpRequestMapping requestMapping;
    private final String httpHandlersQualifierName;
    private final ClassName generatedHttpServiceType;

    public HttpServiceRegistration(ServiceAkimboComponent component) {
        super(component);
        name = component.getType().simpleName() + "_HttpService";
        TypeElement typeElement = component.getTypeElement();
        requestMapping = hasAnnotation(typeElement, RequestMapping.class) ? new HttpRequestMapping(typeElement) : null;
        httpHandlersQualifierName = component.getName() + "HttpHandlers";
        generatedHttpServiceType = ClassName.bestGuess(component.getType().toString() + "_HttpService");
    }

    @Override
    protected Optional<HttpHandlerRegistration> buildMethodRegistration(ExecutableElement method) {
        if (hasAnnotation(method, RequestMapping.class)) {
            return Optional.of(new HttpHandlerRegistration(this, method));
        } else {
            return Optional.empty();
        }
    }

    public String getName() {
        return name;
    }

    public HttpRequestMapping getRequestMapping() {
        return requestMapping;
    }

    public String getHttpHandlersQualifierName() {
        return httpHandlersQualifierName;
    }

    public ClassName getGeneratedHttpServiceType() {
        return generatedHttpServiceType;
    }
}

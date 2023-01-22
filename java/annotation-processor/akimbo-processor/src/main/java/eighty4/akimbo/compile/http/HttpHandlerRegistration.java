package eighty4.akimbo.compile.http;

import com.squareup.javapoet.TypeName;
import eighty4.akimbo.annotation.http.PathVariable;
import eighty4.akimbo.annotation.http.RequestBody;
import eighty4.akimbo.annotation.http.RequestHeader;
import eighty4.akimbo.annotation.http.RequestParam;
import eighty4.akimbo.annotation.http.ResponseStatus;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.service.ServiceMethodRegistration;
import eighty4.akimbo.http.HttpMethod;
import eighty4.akimbo.http.ResponseHeaders;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboElementUtils.getAnnotationValue;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;

public class HttpHandlerRegistration extends ServiceMethodRegistration {
    private final String name;
    private final TypeName componentType;
    private final HttpRequestMapping requestMapping;
    private final HttpHandlerReturnType httpHandlerReturnType;
    private final int successfulResponseStatusCode;
    private final Parameter requestBodyParameter;
    private final Parameter responseHeadersParameter;
    private final List<RequestParameter> requestParameters = new ArrayList<>();

    public HttpHandlerRegistration(HttpServiceRegistration httpServiceRegistration, ExecutableElement executableElement) {
        super(executableElement);
        name = upperCaseFirstChar(getMethodName()) + "_HttpHandler";
        componentType = httpServiceRegistration.getComponent().getType();
        requestMapping = new HttpRequestMapping(executableElement, httpServiceRegistration.getRequestMapping());
        httpHandlerReturnType = HttpHandlerReturnType.fromTypeName(getReturnType());
        successfulResponseStatusCode = getAnnotationValue(executableElement, ResponseStatus.class, Integer.class, "value")
                .orElse(200);
        requestBodyParameter = getParametersWithAnnotation(RequestBody.class).stream()
                .findFirst()
                .orElse(null);
        responseHeadersParameter = getParameters().stream()
                .filter(requestParameter -> requestParameter.getType().equals(TypeName.get(ResponseHeaders.class)))
                .findFirst()
                .orElse(null);
        requestParameters.addAll(getRequestParameters(RequestHeader.class));
        requestParameters.addAll(getRequestParameters(PathVariable.class));
        requestParameters.addAll(getRequestParameters(RequestParam.class));
    }

    private List<RequestParameter> getRequestParameters(Class<? extends Annotation> annotationClass) {
        return getParametersWithAnnotation(annotationClass).stream()
                .map(parameter -> new RequestParameter(parameter, annotationClass))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public HttpMethod getMethod() {
        return requestMapping.getMethod();
    }

    public String getPath() {
        return requestMapping.getPath();
    }

    public TypeName getComponentType() {
        return componentType;
    }

    public HttpHandlerReturnType getHttpHandlerReturnType() {
        return httpHandlerReturnType;
    }

    public int getSuccessfulResponseStatusCode() {
        return successfulResponseStatusCode;
    }

    public Optional<Parameter> getRequestBodyParameter() {
        return Optional.ofNullable(requestBodyParameter);
    }

    public List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public Optional<Parameter> getResponseHeadersParameter() {
        return Optional.ofNullable(responseHeadersParameter);
    }
}

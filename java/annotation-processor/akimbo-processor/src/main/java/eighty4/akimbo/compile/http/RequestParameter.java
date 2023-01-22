package eighty4.akimbo.compile.http;

import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.service.Parameter;

import java.lang.annotation.Annotation;

// todo extend Parameter
public class RequestParameter {

    private final String parameterName;
    private final TypeName parameterType;
    private final Class<? extends Annotation> annotationClass;
    private final String requestParameterName;
    private final boolean required;
    private final String defaultValue;

    RequestParameter(Parameter parameter, Class<? extends Annotation> annotationClass) {
        this.parameterName = parameter.getName();
        this.parameterType = parameter.getType();
        this.annotationClass = annotationClass;

        this.requestParameterName = parameter.getAnnotationRegistration(annotationClass)
                .flatMap(v -> v.getValue("name", "value"))
                .map(Object::toString)
                .orElse(parameterName);
        this.required = parameter.getAnnotationRegistration(annotationClass)
                .flatMap(v -> v.getValue("required"))
                .map(Object::toString)
                .map(Boolean::parseBoolean)
                .orElse(false);
        this.defaultValue = parameter.getAnnotationRegistration(annotationClass)
                .flatMap(v -> v.getValue("defaultValue"))
                .map(Object::toString)
                .orElse(null);
    }

    public String getName() {
        return parameterName;
    }

    public TypeName getType() {
        return parameterType;
    }

    public String getRequestParameterName() {
        return requestParameterName;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}

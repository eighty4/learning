package eighty4.akimbo.compile.service;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceMethodRegistration {
    private final String methodName;
    private final TypeName returnType;
    private final List<Parameter> parameters;

    public ServiceMethodRegistration(ExecutableElement executableElement) {
        methodName = executableElement.getSimpleName().toString();
        returnType = TypeName.get(executableElement.getReturnType());
        parameters = executableElement.getParameters().stream()
                .map(Parameter::new)
                .collect(Collectors.toList());
    }

    public String getMethodName() {
        return methodName;
    }

    public TypeName getReturnType() {
        return returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<Parameter> getParametersWithAnnotation(Class<? extends Annotation> annotationType) {
        return parameters.stream()
                .filter(parameter -> parameter.getAnnotationRegistration(annotationType).isPresent())
                .collect(Collectors.toList());
    }

    public String getParameterNames() {
        return parameters.stream()
                .map(Parameter::getName)
                .collect(Collectors.joining(", "));
    }
}

package eighty4.akimbo.compile.service;

import com.squareup.javapoet.TypeName;
import eighty4.akimbo.annotation.Value;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Parameter {

    public static class ParameterAnnotationRegistration {
        private final TypeName annotationType;
        private final Map<String, Object> members = new HashMap<>();

        ParameterAnnotationRegistration(AnnotationMirror annotation) {
            this.annotationType = TypeName.get(annotation.getAnnotationType());
            annotation.getElementValues().forEach((key, value) -> members.put(key.getSimpleName().toString(), value.getValue()));
        }

        public TypeName getAnnotationType() {
            return annotationType;
        }

        public Optional<Object> getValue(String... memberNames) {
            for (String memberName : memberNames) {
                Object value = members.get(memberName);
                if (value != null) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }

    private final String name;
    private final TypeName type;
    private final Map<TypeName, ParameterAnnotationRegistration> parameterAnnotationRegistrationsByType;

    public Parameter(VariableElement variableElement) {
        name = variableElement.getSimpleName().toString();
        type = TypeName.get(variableElement.asType());

        parameterAnnotationRegistrationsByType = variableElement.getAnnotationMirrors().stream()
                .map(ParameterAnnotationRegistration::new)
                .collect(Collectors.toMap(ParameterAnnotationRegistration::getAnnotationType, Function.identity()));
    }

    public String getName() {
        return name;
    }

    public TypeName getType() {
        return type;
    }

    public Optional<String> getQualifier() {
        return getAnnotationStringValue();
    }

    protected Optional<String> getAnnotationStringValue() {
        return getAnnotationRegistration(Value.class)
                .flatMap(annotationRegistration -> annotationRegistration.getValue("value"))
                .map(Object::toString);
    }

    public Optional<ParameterAnnotationRegistration> getAnnotationRegistration(Class<? extends Annotation> annotationType) {
        return Optional.ofNullable(parameterAnnotationRegistrationsByType.get(TypeName.get(annotationType)));
    }
}

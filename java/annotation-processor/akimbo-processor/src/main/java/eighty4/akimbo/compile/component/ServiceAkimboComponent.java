package eighty4.akimbo.compile.component;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.annotation.Value;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.service.PropertyValueParameter;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboElementUtils.findInjectConstructor;
import static eighty4.akimbo.compile.util.AkimboElementUtils.hasAnnotation;

public class ServiceAkimboComponent extends ComponentReference implements AkimboComponentDefinition {

    private final TypeElement element;

    private final List<Parameter> injectionParameters;

    private final List<PropertyValueParameter> propertyParameters = new ArrayList<>();

    private final ClassName moduleType;

    public ServiceAkimboComponent(TypeElement element) {
        super(ClassName.get(element));
        this.element = element;
        injectionParameters = setInjectionParameters();
        moduleType = ClassName.bestGuess(getType().toString() + "_Module");
    }

    public ServiceAkimboComponent(TypeElement element, ClassName moduleType) {
        this(element, null, moduleType);
    }

    public ServiceAkimboComponent(TypeElement element, String qualifiedName, ClassName moduleType) {
        super(ClassName.get(element), qualifiedName);
        this.element = element;
        injectionParameters = setInjectionParameters();
        this.moduleType = moduleType;
    }

    private List<Parameter> setInjectionParameters() {
        return findInjectConstructor(element)
                .map(injectConstructor -> injectConstructor.getParameters().stream()
                        .map(parameter -> {
                            if (hasAnnotation(parameter, Value.class)) {
                                PropertyValueParameter propertyParameter = new PropertyValueParameter(parameter);
                                propertyParameters.add(propertyParameter);
                                return propertyParameter;
                            } else {
                                return new Parameter(parameter);
                            }
                        })
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public String getPackageName() {
        return getType().packageName();
    }

    @Override
    public List<Parameter> getInjectionParameters() {
        return injectionParameters;
    }

    @Override
    public List<PropertyValueParameter> getPropertyParameters() {
        return propertyParameters;
    }

    @Override
    public TypeElement getTypeElement() {
        return element;
    }

    @Override
    public ClassName getModuleType() {
        return moduleType;
    }

    @Override
    public ClassName getType() {
        return (ClassName) super.getType();
    }
}

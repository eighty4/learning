package eighty4.akimbo.compile.component;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.environment.PropertyValue;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.service.PropertyValueParameter;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface AkimboComponentDefinition extends AkimboComponentReference {

    String getPackageName();

    List<Parameter> getInjectionParameters();

    List<PropertyValueParameter> getPropertyParameters();

    default Set<PropertyValue> getProperties() {
        return getPropertyParameters().stream().map(PropertyValueParameter::getPropertyValue).collect(Collectors.toSet());
    }

    default Set<AkimboComponentReference> getComponentDependencies() {
        return getInjectionParameters().stream()
                .filter(p -> !(p instanceof PropertyValueParameter))
                .map(p -> new ComponentReference(p.getName(), p.getType(), p.getQualifier().orElse(null)))
                .collect(Collectors.toSet());
    }

    TypeElement getTypeElement();

    ClassName getModuleType();

}

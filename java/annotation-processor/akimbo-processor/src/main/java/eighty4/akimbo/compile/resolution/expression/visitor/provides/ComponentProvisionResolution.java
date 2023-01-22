package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.environment.PropertyValue;

import java.util.Set;

public class ComponentProvisionResolution {

    private final String resolvedName;

    private final TypeName resolvedType;

    private final MethodSpec providesMethod;

    private final Set<PropertyValue> propertyValues;

    public ComponentProvisionResolution(String resolvedName,
                                        TypeName resolvedType,
                                        MethodSpec providesMethod,
                                        Set<PropertyValue> propertyValues) {
        this.resolvedName = resolvedName;
        this.resolvedType = resolvedType;
        this.providesMethod = providesMethod;
        this.propertyValues = propertyValues;
    }

    public String getResolvedName() {
        return resolvedName;
    }

    public TypeName getResolvedType() {
        return resolvedType;
    }

    public MethodSpec getProvidesMethod() {
        return providesMethod;
    }

    public Set<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

}

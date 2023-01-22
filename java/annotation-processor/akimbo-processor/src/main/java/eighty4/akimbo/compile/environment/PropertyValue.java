package eighty4.akimbo.compile.environment;

import com.squareup.javapoet.TypeName;

import java.util.Objects;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.propertyKeyToVariableName;

public class PropertyValue {

    private final PropertyReference propertyReference;

    private final TypeName expectedType;

    private final String variableName;

    public PropertyValue(String propertyExpression, TypeName expectedType) {
        this(propertyExpression, null, expectedType);
    }

    public PropertyValue(String propertyExpression, String variableName, TypeName expectedType) {
        this.propertyReference = new PropertyReference(propertyExpression);
        this.expectedType = expectedType;
        this.variableName = variableName == null ? propertyKeyToVariableName(propertyReference.getPropertyKey()) : variableName;
    }

    public PropertyReference getPropertyReference() {
        return propertyReference;
    }

    public TypeName getExpectedType() {
        return expectedType;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyValue that = (PropertyValue) o;
        return propertyReference.equals(that.propertyReference) &&
                expectedType.equals(that.expectedType) &&
                variableName.equals(that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyReference, expectedType, variableName);
    }

    @Override
    public String toString() {
        return "PropertyReferenceResolution{" +
                "propertyReference=" + propertyReference +
                ", expectedType=" + expectedType +
                ", variableName='" + variableName + '\'' +
                '}';
    }
}

package eighty4.akimbo.compile.service;

import eighty4.akimbo.compile.environment.PropertyValue;

import javax.lang.model.element.VariableElement;

public class PropertyValueParameter extends Parameter {

    private final PropertyValue propertyValue;

    public PropertyValueParameter(VariableElement parameter) {
        super(parameter);
        propertyValue = new PropertyValue(getAnnotationStringValue().orElseThrow(), getName(), getType());
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

}

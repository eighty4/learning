package eighty4.akimbo.compile.environment;

import java.util.Objects;
import java.util.Optional;

public class PropertyReference {

    private final String propertyKey;

    private final String defaultValue;

    public PropertyReference(String propertyExpression) {
        if (propertyExpression.contains(":")) {
            String[] split = propertyExpression.split(":");
            if (split.length > 2) {
                throw new IllegalArgumentException("Property expression (" + propertyExpression + ") is invalid");
            } else {
                propertyKey = split[0];
                defaultValue = split[1];
            }
        } else {
            propertyKey = propertyExpression;
            defaultValue = null;
        }
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyReference that = (PropertyReference) o;
        return propertyKey.equals(that.propertyKey) &&
                Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyKey, defaultValue);
    }
}

package eighty4.akimbo.compile.source.provides;

import com.squareup.javapoet.CodeBlock;
import eighty4.akimbo.compile.environment.PropertyValue;
import eighty4.akimbo.compile.source.SourceForm;
import eighty4.akimbo.environment.EnvironmentAccess;

import java.util.Set;

public class EnvironmentValueAssignments implements SourceForm<CodeBlock> {

    private final Set<PropertyValue> propertyValues;

    public EnvironmentValueAssignments(Set<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public CodeBlock toSpec() {
        CodeBlock.Builder builder = CodeBlock.builder();
        propertyValues.forEach(propertyValue -> builder.addStatement("$T $L = $T.getProperty($S, $L, $T.class)",
                propertyValue.getExpectedType(),
                propertyValue.getVariableName(),
                EnvironmentAccess.class,
                propertyValue.getPropertyReference().getPropertyKey(),
                propertyValue.getPropertyReference().getDefaultValue().map(dv -> "\"" + dv + "\"").orElse("null"),
                propertyValue.getExpectedType()));
        return builder.build();
    }

}

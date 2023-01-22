package eighty4.akimbo.compile.resolution.expression.visitor.references;

import com.squareup.javapoet.ClassName;

import java.util.Set;

public class ExpressionReferences {

    private final Set<String> componentReferences;

    private final Set<ClassName> typeReferences;

    public ExpressionReferences(Set<String> componentReferences, Set<ClassName> typeReferences) {
        this.componentReferences = componentReferences;
        this.typeReferences = typeReferences;
    }

    public Set<String> getComponentReferences() {
        return componentReferences;
    }

    public Set<ClassName> getTypeReferences() {
        return typeReferences;
    }

}

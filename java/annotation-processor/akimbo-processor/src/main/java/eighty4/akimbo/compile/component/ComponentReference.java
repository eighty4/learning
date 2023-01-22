package eighty4.akimbo.compile.component;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Objects;
import java.util.Optional;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

public class ComponentReference implements AkimboComponentReference {

    private final String name;

    private final TypeName type;

    private final String qualifiedName;

    public ComponentReference(ClassName type) {
        this(lowerCaseFirstChar(type.simpleName()), type, null);
    }

    public ComponentReference(ClassName type, String qualifiedName) {
        this(lowerCaseFirstChar(type.simpleName()), type, qualifiedName);
    }

    public ComponentReference(String name, TypeName type) {
        this(name, type, null);
    }

    public ComponentReference(String name, TypeName type, String qualifiedName) {
        this.name = name;
        this.type = type;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String getName() {
        return getQualifiedName().orElse(name);
    }

    @Override
    public TypeName getType() {
        return type;
    }

    @Override
    public Optional<String> getQualifiedName() {
        return Optional.ofNullable(qualifiedName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentReference)) return false;
        ComponentReference that = (ComponentReference) o;
        return type.equals(that.type) && Objects.equals(qualifiedName, that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, type);
    }

    @Override
    public String toString() {
        return "#" + name;
    }
}

package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;

class TypeContext {

    private final TypeName typeName;

    private final TypeElement typeElement;

    TypeContext(TypeName typeName) {
        this(typeName, null);
    }

    TypeContext(TypeName typeName, TypeElement typeElement) {
        this.typeName = typeName;
        this.typeElement = typeElement;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

}

package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Optional;

public class ElementResolver {

    private final Types typeUtils;

    private final Elements elementUtils;

    public ElementResolver(Types typeUtils, Elements elementUtils) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public TypeElement getTypeElement(TypeMirror typeMirror) {
        return (TypeElement) typeUtils.asElement(typeMirror);
    }

    public TypeElement getTypeElement(ClassName className) {
        return elementUtils.getTypeElement(className.toString());
    }

    public Optional<TypeElement> getTypeElement(TypeName typeName) {
        return Optional.ofNullable(elementUtils.getTypeElement(typeName.toString()));
    }
}

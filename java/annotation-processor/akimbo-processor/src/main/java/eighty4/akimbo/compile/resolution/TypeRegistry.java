package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.debug.EventWriter;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeRegistry {

    private final Set<TypeElement> userSources = new HashSet<>();

    private final Map<ClassName, TypeElement> typeElements = new HashMap<>();

    private final ElementResolver elementResolver;

    private final EventWriter eventWriter;

    public TypeRegistry(ElementResolver elementResolver, EventWriter eventWriter) {
        this.elementResolver = elementResolver;
        this.eventWriter = eventWriter;
    }

    public Map<ClassName, TypeElement> getTypeElements() {
        return typeElements;
    }

    public void addUserSource(TypeElement typeElement) {
        userSources.add(typeElement);
        addTypeElement(typeElement);
    }

    public void addTypeElement(TypeElement typeElement) {
        addTypeElement(ClassName.get(typeElement), typeElement);
    }

    private void addTypeElement(ClassName typeName, TypeElement typeElement) {
        getTypeElements().put(typeName, typeElement);
        eventWriter.writeSourceAdded(typeElement);
        RegistrationActivityChannel.getInstance().announceTypeRegistered(typeName);
    }

    public TypeElement getTypeElement(ClassName className) {
        return getTypeElements().computeIfAbsent(className, elementResolver::getTypeElement);
    }

    public Collection<TypeElement> getUserSources() {
        return userSources;
    }

}

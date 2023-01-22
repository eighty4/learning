package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

class TypeCache {

    private final ProcessorContext processorContext;

    private final Map<String, TypeContext> byComponentName = new HashMap<>();

    private final Map<ClassName, TypeContext> byComponentType = new HashMap<>();

    TypeCache(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    TypeContext lookup(String componentName) {
        return byComponentName.compute(componentName, (s, typeContext) -> {
            if (typeContext == null) {
                AkimboComponentDefinition componentDefinition = processorContext.getComponentRegistry().getDefinitionByName(componentName);
                TypeElement typeElement = componentDefinition.getTypeElement();
                if (typeElement == null) {
                    typeElement = processorContext.getTypeRegistry().getTypeElement(ClassName.bestGuess(componentDefinition.getType().toString()));
                }
                typeContext = new TypeContext(componentDefinition.getType(), typeElement);
            }
            return typeContext;
        });
    }

    TypeContext lookup(ClassName typeName) {
        return byComponentType.compute(typeName, (s, typeContext) -> {
            if (typeContext == null) {
                TypeElement typeElement = processorContext.getTypeRegistry().getTypeElement(typeName);
                typeContext = new TypeContext(typeName, typeElement);
            }
            if (typeContext.getTypeElement() == null) {
                System.out.println("asdf");
            }
            return typeContext;
        });
    }

}

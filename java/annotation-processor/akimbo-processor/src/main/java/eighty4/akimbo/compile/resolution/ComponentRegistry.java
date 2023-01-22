package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.AkimboComponentReference;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ComponentRegistry {

    private final ComponentGraph componentGraph;

    public ComponentRegistry(ComponentGraph componentGraph) {
        this.componentGraph = componentGraph;
    }

    public AkimboComponentDefinition getDefinitionByReference(AkimboComponentReference componentReference) {
        return componentGraph.getComponent(componentReference).getDefinition();
    }

    public Collection<AkimboComponentReference> getReferencesByFilter(Predicate<ComponentResolution> componentResolutionPredicate) {
        return componentGraph.getComponents().stream()
                .filter(componentResolutionPredicate)
                .map(ComponentResolution::getReference)
                .collect(Collectors.toList());
    }

    public AkimboComponentDefinition getDefinitionByName(String name) {
        return getResolutionByName(name)
                .map(ComponentResolution::getDefinition)
                .orElse(null);
    }

    private Optional<ComponentResolution> getResolutionByName(String name) {
        return componentGraph.getComponents().stream()
                .filter(componentResolution -> componentResolution.getReference().getName().equals(name))
                .findFirst();
    }

    public ClassName getModuleTypeForComponent(AkimboComponentReference componentReference) {
        ComponentResolution component = componentGraph.getComponent(componentReference);
        ModuleResolution moduleResolution = component.getModuleResolution();
        if (moduleResolution == null) {
            throw new ComponentResolutionException("Component " + componentReference + " is missing a module resolution");
        }
        return moduleResolution.getModuleType();
    }

}

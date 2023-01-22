package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVED;
import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVING;

public class ModuleResolution {

    private final ClassName moduleType;

    private final ModuleSource moduleSource;

    private final Collection<ComponentResolution> componentResolutions;

    private final Collection<ComponentResolution> unresolvedComponents;

    private ResolutionState state;

    public ModuleResolution(ClassName moduleType, ModuleSource moduleSource, ComponentResolution componentResolution) {
        this(moduleType, moduleSource, List.of(componentResolution));
    }

    public ModuleResolution(ClassName moduleType, ModuleSource moduleSource, Collection<ComponentResolution> componentResolutions) {
        this(moduleType, moduleSource, componentResolutions, ResolutionState.UNRESOLVABLE);
        updateResolutionState();
    }

    public ModuleResolution(ClassName moduleType, ModuleSource moduleSource, Collection<ComponentResolution> componentResolutions, ResolutionState state) {
        this.moduleType = moduleType;
        this.moduleSource = moduleSource;
        this.componentResolutions = componentResolutions;
        componentResolutions.forEach(componentResolution -> componentResolution.setModuleResolution(this));
        unresolvedComponents = new ArrayDeque<>(componentResolutions);
        this.state = state;
    }

    public void updateResolutionState() {
        unresolvedComponents.removeIf(componentResolution -> componentResolution.getState().isResolvable());
        state = unresolvedComponents.isEmpty() ? ResolutionState.RESOLVABLE : ResolutionState.UNRESOLVABLE;
    }

    public ResolutionState getState() {
        return state;
    }

    public void setResolved() {
        state = RESOLVED;
    }

    public void setResolving() {
        state = RESOLVING;
    }

    public ClassName getModuleType() {
        return moduleType;
    }

    public ModuleSource getModuleSource() {
        return moduleSource;
    }

    public Collection<ComponentResolution> getComponentResolutions() {
        return new ArrayDeque<>(componentResolutions);
    }

    @Override
    public String toString() {
        return moduleType.simpleName();
    }
}

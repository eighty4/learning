package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.MethodSpec;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.source.provides.ProvidesComponentMethod;

import java.util.Objects;
import java.util.Optional;

import static eighty4.akimbo.compile.resolution.ResolutionState.*;

public class ComponentResolution {

    private AkimboComponentReference reference;

    private AkimboComponentDefinition definition;

    private ResolutionState state;

    private ModuleResolution moduleResolution;

    public ComponentResolution(AkimboComponentReference reference, ResolutionState state) {
        this.reference = reference;
        this.state = state;
        this.definition = null;
    }

    ComponentResolution(AkimboComponentDefinition definition, ResolutionState state) {
        this.reference = definition;
        this.state = state;
        this.definition = definition;
    }

    public AkimboComponentReference getReference() {
        return reference;
    }

    protected void setReference(AkimboComponentReference reference) {
        if (this.reference != null) {
            if (!this.reference.equals(reference)) {
                throw new IllegalStateException("Only an equal component reference can be set after construction");
            } else if (state == UNREGISTERED) {
                throw new IllegalStateException("Only an UNREGISTERED component can be updated with a new reference object");
            }
        }
        this.reference = reference;
    }

    public AkimboComponentDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(AkimboComponentDefinition definition) {
        if (reference == null) {
            setReference(definition);
        }
        if (!reference.equals(definition)) {
            throw new IllegalStateException("Only an equal component definition can be set after construction");
        } else if (state == UNREGISTERED) {
            throw new IllegalStateException("Only an UNREGISTERED component can be updated with a new definition object");
        } else if (this.definition != null) {
            throw new IllegalStateException("ComponentResolution already has a component definition");
        }
        this.definition = definition;
    }

    public ResolutionState getState() {
        return state;
    }

    public void setState(ResolutionState state) {
        if (state == UNREGISTERED) {
            throw new IllegalArgumentException("Only initial resolution state can be UNREGISTERED");
        } else if (state == RESOLVING && this.state != RESOLVABLE) {
            throw new IllegalArgumentException("Only RESOLVABLE can transition to RESOLVING");
        } else if (state == RESOLVED && !this.state.isResolvable()) {
            throw new IllegalArgumentException("Only RESOLVING can transition to RESOLVED");
        }
        this.state = state;
        Optional.ofNullable(moduleResolution).ifPresent(ModuleResolution::updateResolutionState);
    }

    public ModuleResolution getModuleResolution() {
        return moduleResolution;
    }

    public void setModuleResolution(ModuleResolution moduleResolution) {
        if (this.moduleResolution != null) {
            throw new IllegalStateException("Module resolution can not be set more than once");
        }
        this.moduleResolution = moduleResolution;
    }

    public MethodSpec getProvidesMethod() {
        return new ProvidesComponentMethod(definition).toSpec();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentResolution that = (ComponentResolution) o;
        return Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public String toString() {
        if (reference == null) {
            return "UNKNOWN of " + getClass().getSimpleName();
        }
        return reference.toString();
    }

}

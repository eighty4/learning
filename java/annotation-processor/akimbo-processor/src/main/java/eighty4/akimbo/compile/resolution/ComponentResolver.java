package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.resolution.expression.ExpressionResolver;
import eighty4.akimbo.compile.resolution.expression.ResolutionExpression;
import eighty4.akimbo.compile.service.ServiceRegistry;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVABLE;
import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVED;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNREGISTERED;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNRESOLVABLE;

public class ComponentResolver {

    private final ProcessorContext processorContext;

    private final ExpressionResolver expressionResolver;

    private final ComponentGraph componentGraph;

    private final ServiceRegistry serviceRegistry;

    public ComponentResolver(ProcessorContext processorContext, ExpressionResolver expressionResolver,
                             ComponentGraph componentGraph, ServiceRegistry serviceRegistry) {
        this.processorContext = processorContext;
        this.expressionResolver = expressionResolver;
        this.componentGraph = componentGraph;
        this.serviceRegistry = serviceRegistry;
    }

    public void addComponentResolution(ComponentResolution componentResolution) {
        AkimboComponentDefinition componentDefinition = componentResolution.getDefinition();
        if (componentDefinition instanceof ServiceAkimboComponent) {
            serviceRegistry.addComponent((ServiceAkimboComponent) componentDefinition);
        }
        componentGraph.addComponentResolution(componentResolution);
        RegistrationActivityChannel.getInstance().announceComponentRegistered(componentResolution.getReference());
        if (componentResolution.getState().isResolvable()) {
            addComponentReferences(componentResolution);
            updateDependentResolutionState(componentResolution);
        } else {
            attemptResolution(componentResolution);
        }
    }

    public void addComponentFromElement(TypeElement typeElement) {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(typeElement);
        boolean isServiceComponent = serviceRegistry.addComponent(componentDefinition);
        ResolutionState initialState = isServiceComponent ? UNRESOLVABLE : UNREGISTERED;
        ComponentResolution componentResolution = componentGraph.addComponentDefinition(componentDefinition, initialState);
        if (componentResolution.getState().isRegistered()) {
            attemptResolution(componentResolution);
        }
    }

    public ComponentResolution createComponentResolutionFromDefinition(AkimboComponentDefinition componentDefinition, ResolutionState resolutionState) {
        ComponentResolution componentResolution = new ComponentResolution(componentDefinition, resolutionState);
        addComponentResolution(componentResolution);
        return componentResolution;
    }

    public ComponentResolution createComponentResolutionFromExpression(ResolutionExpression resolutionExpression, ClassName moduleType) {
        ComponentResolution componentResolution = expressionResolver.createComponentResolution(resolutionExpression, moduleType);
        if (componentResolution.getReference() != null) {
            addComponentResolution(componentResolution);
        }
        return componentResolution;
    }

    public void addComponentReferences(ComponentResolution componentResolution) {
        componentResolution.getDefinition().getComponentDependencies().forEach(dependency -> componentGraph.addComponentReference(dependency, componentResolution));
    }

    private void attemptResolution(ComponentResolution componentResolution) {
        Set<AkimboComponentReference> componentDependencies = componentResolution.getDefinition().getComponentDependencies();
        boolean resolvable = componentDependencies.isEmpty() || componentDependencies.stream()
                .reduce(true, (_unresolvable, dependencyReference) -> {
                    boolean depResolvable = false;
                    ComponentResolution dependencyResolution = componentGraph.getComponent(dependencyReference);
                    if (dependencyResolution == null) {
                        dependencyResolution = new ComponentResolution(dependencyReference, UNRESOLVABLE);
                        componentGraph.addComponentResolution(dependencyResolution);
                    } else {
                        if (dependencyResolution.getState() == UNREGISTERED) {
                            if (componentResolution.getDefinition() == null) {
                                componentResolution.setState(UNRESOLVABLE);
                            } else {
                                attemptResolution(dependencyResolution);
                            }
                        }
                        depResolvable = dependencyResolution.getState().isResolvable();
                    }
                    componentGraph.setComponentDependency(componentResolution, dependencyResolution);
                    return depResolvable;
                }, Boolean::logicalAnd);
        if (resolvable) {
            setResolvable(componentResolution);
            Optional.ofNullable(componentResolution.getModuleResolution()).ifPresent(ModuleResolution::updateResolutionState);
        }
    }

    private void setResolvable(ComponentResolution componentResolution) {
        if (componentResolution.getState().isResolvable()) {
            throw new IllegalStateException("Should not be trying to set resolvable component as resolvable");
        }
        componentResolution.setState(RESOLVABLE);
        updateDependentResolutionState(componentResolution);
        processorContext.getEventWriter().writeComponentStateChange(componentResolution);
    }

    private void updateDependentResolutionState(ComponentResolution componentResolution) {
        componentGraph.getDependents(componentResolution).stream()
                .filter(dependentComponent -> !dependentComponent.getState().isResolvable())
                .filter(this::isResolvable)
                .forEach(this::setResolvable);
    }

    private boolean isResolvable(ComponentResolution componentResolution) {
        return componentGraph.getDependencies(componentResolution).stream()
                .allMatch(dependencyComponent -> dependencyComponent.getState().isResolvable());
    }

    public Collection<ComponentResolution> getResolvableComponents() {
        return componentGraph.getComponents(RESOLVABLE);
    }

    public boolean hasResolvedAllComponents() {
        return componentGraph.getComponents().stream()
                .map(ComponentResolution::getState)
                .allMatch(resolutionState -> resolutionState == RESOLVED || !resolutionState.isRegistered());
    }

}

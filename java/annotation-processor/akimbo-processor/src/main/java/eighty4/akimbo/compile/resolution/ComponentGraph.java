package eighty4.akimbo.compile.resolution;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.AkimboComponentReference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.resolution.ResolutionState.UNREGISTERED;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNRESOLVABLE;

@SuppressWarnings("UnstableApiUsage")
public class ComponentGraph {

    private final Map<AkimboComponentReference, ComponentResolution> components = new HashMap<>();

    private final MutableGraph<ComponentResolution> dependencyGraph = GraphBuilder.directed().allowsSelfLoops(false).build();

    /**
     * Adds a {@link ComponentResolution} to the graph for a given {@link AkimboComponentDefinition}, or updates an existing
     * {@link ComponentResolution} with the given {@link AkimboComponentDefinition} and {@link ResolutionState}.
     * <p>
     * {@link ResolutionState} for an existing component can not be UNREGISTERED.
     *
     * @param componentDefinition component to add to the resolution graph
     * @param resolutionState     initial or updated resolution state of component
     * @return created or updated {@link ComponentResolution}
     */
    public ComponentResolution addComponentDefinition(AkimboComponentDefinition componentDefinition, ResolutionState resolutionState) {
        ComponentResolution componentResolution = components.get(componentDefinition);
        if (componentResolution == null) {
            componentResolution = new ComponentResolution(componentDefinition, resolutionState);
            addComponentResolution(componentResolution);
        } else {
            componentResolution.setDefinition(componentDefinition);
            if (resolutionState != UNREGISTERED) {
                componentResolution.setState(resolutionState);
            }
        }
        return componentResolution;
    }

    /**
     * Creates a dependent-dependency relationship between two components by adding an {@link AkimboComponentReference}
     * to the graph with an edge connecting it to its dependent {@link ComponentResolution}.
     *
     * @param dependencyReference reference to a component
     * @param dependentResolution component resolution with dependency on added reference
     * @return true if {@link AkimboComponentReference} is for a component that is already resolvable
     */
    public boolean addComponentReference(AkimboComponentReference dependencyReference, ComponentResolution dependentResolution) {
        ComponentResolution dependencyResolution = components.get(dependencyReference);
        boolean resolvable = false;
        if (dependencyResolution == null) {
            dependencyResolution = new ComponentResolution(dependencyReference, UNRESOLVABLE);
            addComponentResolution(dependencyResolution);
        } else {
            resolvable = dependencyResolution.getState().isResolvable();
        }
        setComponentDependency(dependentResolution, dependencyResolution);
        return resolvable;
    }

    /**
     * Add a {@link ComponentResolution} to the graph.
     *
     * @param componentResolution resolution state for a component
     */
    public void addComponentResolution(ComponentResolution componentResolution) {
        if (componentResolution.getReference() == null) {
            throw new IllegalArgumentException("Cannot add a component resolution without a reference");
        }
        ComponentResolution existingResolution = components.get(componentResolution.getReference());
        if (existingResolution != null) {
            if (existingResolution.getState().isResolvable() && !componentResolution.getState().isResolvable()) {
                throw new IllegalStateException("Cannot overwrite a component resolution");
            } else {
                swapComponentResolutions(componentResolution, existingResolution);
            }
        }
        components.put(componentResolution.getReference(), componentResolution);
        dependencyGraph.addNode(componentResolution);
    }

    private void swapComponentResolutions(ComponentResolution replacementResolution, ComponentResolution existingResolution) {
        Set<ComponentResolution> predecessors = dependencyGraph.predecessors(existingResolution);
        Set<ComponentResolution> successors = dependencyGraph.successors(existingResolution);
        components.put(replacementResolution.getReference(), replacementResolution);
        dependencyGraph.removeNode(existingResolution);
        dependencyGraph.addNode(replacementResolution);
        predecessors.forEach(predecessor -> dependencyGraph.putEdge(predecessor, replacementResolution));
        successors.forEach(successor -> dependencyGraph.putEdge(replacementResolution, successor));
    }

    /**
     * Retrieves a {@link ComponentResolution} by its {@link AkimboComponentReference}.
     *
     * @param componentReference reference to a component
     * @return resolution state for a component
     */
    public ComponentResolution getComponent(AkimboComponentReference componentReference) {
        return components.get(componentReference);
    }

    /**
     * Retrieves any {@link ComponentResolution} objects dependent on a given component
     *
     * @param componentResolution resolution state of component to retrieve dependents of
     * @return dependent component resolutions
     */
    public Collection<ComponentResolution> getDependents(ComponentResolution componentResolution) {
        return dependencyGraph.predecessors(componentResolution);
    }

    /**
     * Retrieves any {@link ComponentResolution} objects that are dependencies of a given component
     *
     * @param componentResolution resolution state of component to retrieve dependencies of
     * @return dependency component resolutions
     */
    public Collection<ComponentResolution> getDependencies(ComponentResolution componentResolution) {
        return dependencyGraph.successors(componentResolution);
    }

    /**
     * Retrieves all registered and unregistered components.
     *
     * @return all component resolutions in graph
     */
    public Collection<ComponentResolution> getComponents() {
        return components.values();
    }

    /**
     * Retrieves registered and unregistered components that match the given predicate.
     *
     * @return filtered component resolutions in graph
     */
    public Collection<ComponentResolution> getComponents(Predicate<ComponentResolution> filterPredicate) {
        return getComponents().stream().filter(filterPredicate).collect(Collectors.toList());
    }

    /**
     * Retrieves registered and unregistered components in the given resolution state.
     *
     * @return component resolutions in graph of the given resolution state
     */
    public Collection<ComponentResolution> getComponents(ResolutionState resolutionState) {
        return getComponents(componentResolution -> componentResolution.getState() == resolutionState);
    }

    public void setComponentDependency(ComponentResolution componentResolution, ComponentResolution dependencyResolution) {
        dependencyGraph.putEdge(componentResolution, dependencyResolution);
    }
}

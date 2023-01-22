package eighty4.akimbo.compile.resolution.expression;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ComponentResolutionException;
import eighty4.akimbo.compile.resolution.RegistrationActivityChannel;
import eighty4.akimbo.compile.resolution.expression.visitor.provides.ComponentProvisionResolution;
import eighty4.akimbo.compile.resolution.expression.visitor.provides.ComponentProvisionResolver;
import eighty4.akimbo.compile.resolution.expression.visitor.references.ComponentReferenceCollector;
import eighty4.akimbo.compile.resolution.expression.visitor.references.ExpressionReferences;
import eighty4.akimbo.expressions.AkimboExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVABLE;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNRESOLVABLE;

public class ComponentExpressionResolution extends ComponentResolution {

    private final ClassName moduleType;

    private final ProcessorContext processorContext;

    private final AkimboExpression akimboExpression;

    private final Set<AkimboComponentReference> resolvedComponentDependencies = new HashSet<>();

    private final Set<String> unresolvedComponentNames = new HashSet<>();

    private final Set<ClassName> unresolvedComponentTypes;

    private final String explicitComponentName;

    private MethodSpec providesMethod;

    public ComponentExpressionResolution(ClassName moduleType, ProcessorContext processorContext, ResolutionExpression resolutionExpression) {
        super(null, UNRESOLVABLE);
        this.moduleType = moduleType;
        this.processorContext = processorContext;

        try {
            explicitComponentName = resolutionExpression.getExplicitName();
            akimboExpression = AkimboExpression.fromString(resolutionExpression.getExpression());

            ComponentRegistry componentRegistry = processorContext.getComponentRegistry();
            ExpressionReferences expressionReferences = akimboExpression.evaluate(new ComponentReferenceCollector(processorContext));

            expressionReferences.getComponentReferences().forEach(componentName -> {
                AkimboComponentDefinition componentDefinition = componentRegistry.getDefinitionByName(componentName);
                if (componentDefinition == null) {
                    unresolvedComponentNames.add(componentName);
                } else {
                    resolvedComponentDependencies.add(componentDefinition);
                }
            });
            unresolvedComponentTypes = expressionReferences.getTypeReferences().stream()
                    .filter(typeName -> processorContext.getTypeRegistry().getTypeElement(typeName) == null)
                    .collect(Collectors.toSet());

            if (isExpressionResolvable()) {
                resolveExpression();
            } else {
                RegistrationActivityChannel rac = RegistrationActivityChannel.getInstance();
                if (!unresolvedComponentTypes.isEmpty()) {
                    unresolvedComponentTypes.forEach(typeName -> rac.onTypeRegistered(typeName, this::removeUnresolvedComponentType));
                }
                if (!unresolvedComponentNames.isEmpty()) {
                    unresolvedComponentNames.forEach(componentName -> rac.onComponentRegistered(componentName, this::removeUnresolvedComponent));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error initializing component resolution from expression: " + resolutionExpression.getExpression(), e);
        }
    }

    public void resolveExpression() {
        try {
            ComponentProvisionResolution componentProvisionResolution = akimboExpression.evaluate(new ComponentProvisionResolver(processorContext, explicitComponentName));
            providesMethod = componentProvisionResolution.getProvidesMethod();
            this.setDefinition(new ExpressionAkimboComponent(componentProvisionResolution, resolvedComponentDependencies, moduleType));
            this.setState(RESOLVABLE);
        } catch (Exception e) {
            throw new ComponentResolutionException("Failed to resolve component for module " + moduleType.simpleName() + " from expression \"" + akimboExpression.getExpression() + "\"", e);
        }
    }

    private void removeUnresolvedComponent(AkimboComponentReference componentReference) {
        unresolvedComponentNames.remove(componentReference.getName());
        resolvedComponentDependencies.add(componentReference);
        if (isExpressionResolvable()) {
            resolveExpression();
        }
    }

    private void removeUnresolvedComponentType(ClassName componentType) {
        unresolvedComponentTypes.remove(componentType);
        if (isExpressionResolvable()) {
            resolveExpression();
        }
    }

    @Override
    public MethodSpec getProvidesMethod() {
        return providesMethod;
    }

    @Override
    public String toString() {
        if (getReference() == null) {
            return akimboExpression.getExpression();
        } else {
            return super.toString() + "(" + akimboExpression.getExpression() + ")";
        }
    }

    boolean isExpressionResolvable() {
        return unresolvedComponentNames.isEmpty() && unresolvedComponentTypes.isEmpty();
    }

}

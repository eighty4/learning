package eighty4.akimbo.compile.resolution.expression;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.resolution.ComponentResolution;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVABLE;

public class ExpressionResolver {

    private final ProcessorContext processorContext;

    private final Deque<ComponentExpressionResolution> pendingExpressions = new ArrayDeque<>();

    public ExpressionResolver(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    public Collection<ComponentResolution> getResolvableExpressions() {
        Deque<ComponentResolution> returning = new ArrayDeque<>();
        pendingExpressions.removeIf(componentResolution -> {
            boolean isResolvable = componentResolution.getState() == RESOLVABLE;
            if (isResolvable) {
                returning.add(componentResolution);
            }
            return isResolvable;
        });
        return returning;
    }

    public ComponentResolution createComponentResolution(ResolutionExpression resolutionExpression, ClassName moduleType) {
        ComponentExpressionResolution componentResolution = new ComponentExpressionResolution(moduleType, processorContext, resolutionExpression);
        if (!componentResolution.isExpressionResolvable()) {
            pendingExpressions.add(componentResolution);
        }
        return componentResolution;
    }

}

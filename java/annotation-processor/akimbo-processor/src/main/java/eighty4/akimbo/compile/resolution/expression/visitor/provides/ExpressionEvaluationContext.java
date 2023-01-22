package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.environment.PropertyValue;
import eighty4.akimbo.compile.resolution.ComponentResolutionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

class ExpressionEvaluationContext {

    private final TypeCache typeCache;

    private final Stack<TypeContext> eval = new Stack<>();

    private final Stack<Integer> argumentCounters = new Stack<>();

    private final String explicitComponentName;

    private final Set<PropertyValue> propertyValues = new HashSet<>();

    private final List<ParameterSpec> parameterSpecs = new ArrayList<>();

    private String resolvedComponentName;

    ExpressionEvaluationContext(ProcessorContext processorContext, String explicitComponentName) {
        typeCache = new TypeCache(processorContext);
        this.explicitComponentName = explicitComponentName;
    }

    void start(String componentName) {
        TypeContext typeContext = typeCache.lookup(componentName);
        parameterSpecs.add(ParameterSpec.builder(typeContext.getTypeName(), componentName).build());
        eval.push(typeContext);
    }

    TypeContext start(ClassName typeReference) {
        if (isPrimary()) {
            resolvedComponentName = lowerCaseFirstChar(typeReference.simpleName());
        }
        return eval.push(typeCache.lookup(typeReference));
    }

    void forward(String resolvedComponentName, TypeName typeName) {
        this.resolvedComponentName = resolvedComponentName;
        eval.pop();
        eval.push(new TypeContext(typeName));
    }

    void finish() {
        eval.pop();
    }

    void literal(Class<?> literalType) {
        literal(TypeName.get(literalType));
    }

    void literal(TypeName literalType) {
        if (eval.isEmpty()) {
            eval.push(new TypeContext(literalType, null));
        }
    }

    void expectArgumentCapture(int argumentCount) {
        argumentCounters.push(argumentCount);
    }

    void decrementArgumentCapture() {
        argumentCounters.push(argumentCounters.pop() - 1);
    }

    int remainingArguments() {
        return argumentCounters.peek();
    }

    void resolveArgumentCapture() {
        if (remainingArguments() > 0) {
            throw new IllegalStateException("asdf");
        }
        argumentCounters.pop();
    }

    TypeContext current() {
        return eval.peek();
    }

    PropertyValue property(String propertyKey) {
        ClassName propertyType = ClassName.get(String.class);
        PropertyValue propertyReference = new PropertyValue(propertyKey, propertyType);
        propertyValues.add(propertyReference);
        if (isPrimary()) {
            // todo explicit type and casting
            literal(propertyType);
        }
        return propertyReference;
    }

    String getResolvedComponentName() {
        String result = explicitComponentName == null ? resolvedComponentName : explicitComponentName;
        if (result == null) {
            throw new ComponentResolutionException("Unable to resolve a component name from expression");
        }
        return result;
    }

    Set<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    List<ParameterSpec> getParameterSpecs() {
        return parameterSpecs;
    }

    boolean isPrimary() {
        return eval.size() == 0 || eval.size() == 1;
    }

}

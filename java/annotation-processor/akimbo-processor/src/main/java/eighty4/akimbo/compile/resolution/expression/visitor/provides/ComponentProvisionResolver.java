package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import dagger.Provides;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.resolution.ComponentResolutionException;
import eighty4.akimbo.compile.source.provides.EnvironmentValueAssignments;
import eighty4.akimbo.expressions.AkimboExpressionValueListener;
import eighty4.akimbo.expressions.AkimboExpressionsParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;

public class ComponentProvisionResolver extends AkimboExpressionValueListener<ComponentProvisionResolution> {

    private final ProcessorContext processorContext;

    private final ExpressionEvaluationContext evaluationContext;

    private final CodeBlock.Builder bodyBuilder = CodeBlock.builder().add("return ");

    public ComponentProvisionResolver(ProcessorContext processorContext, String explicitComponentName) {
        this.processorContext = processorContext;
        evaluationContext = new ExpressionEvaluationContext(processorContext, explicitComponentName);
    }

    @Override
    public void exitExpression(AkimboExpressionsParser.ExpressionContext ctx) {
        boolean isArgumentListExpression = ctx.getParent() != null && ctx.getParent() instanceof AkimboExpressionsParser.ArgumentListContext;
        if (isArgumentListExpression) {
            evaluationContext.decrementArgumentCapture();
            if (evaluationContext.remainingArguments() > 0) {
                bodyBuilder.add(", ");
            }
        }
        if (!evaluationContext.isPrimary()) {
            if (ctx.ongoingExpression() != null || ctx.instanceCreation() != null || ctx.componentReference() != null) {
                evaluationContext.finish();
            }
        }
    }

    @Override
    public void enterInstanceCreation(AkimboExpressionsParser.InstanceCreationContext ctx) {
        bodyBuilder.add("new ");
    }

    @Override
    public void enterAkimboTypeReference(AkimboExpressionsParser.AkimboTypeReferenceContext ctx) {
        String typeReference = processorContext.getAkimboSourcesPackageName() + "." + ctx.name.getText();
        boolean creatingInstance = ctx.getParent().getParent() instanceof AkimboExpressionsParser.InstanceCreationContext;
        handleTypeReference(typeReference, creatingInstance);
    }

    @Override
    public void enterUserTypeReference(AkimboExpressionsParser.UserTypeReferenceContext ctx) {
        boolean creatingInstance = ctx.getParent().getParent() instanceof AkimboExpressionsParser.InstanceCreationContext;
        handleTypeReference(ctx.name.getText(), creatingInstance);
    }

    private void handleTypeReference(String typeReference, boolean creatingInstance) {
        ClassName typeName = ClassName.bestGuess(typeReference);
        TypeElement typeElement = evaluationContext.start(typeName).getTypeElement();
        if (creatingInstance) {
            typeElement.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                    .filter(e -> !e.getModifiers().contains(Modifier.PRIVATE))
                    .findFirst()
                    .orElseThrow(() -> new ComponentResolutionException("Unable to resolve public constructor for " + typeElement.getSimpleName()));
        }
        bodyBuilder.add("$T", typeName);
    }

    @Override
    public void enterComponentReference(AkimboExpressionsParser.ComponentReferenceContext ctx) {
        String referencedComponentName = ctx.name.getText();
        evaluationContext.start(referencedComponentName);
        bodyBuilder.add(referencedComponentName);
    }

    @Override
    public void enterMethodOrProperty(AkimboExpressionsParser.MethodOrPropertyContext ctx) {
        if (ctx.arguments() == null) {
            handlePropertyAccess(ctx.name.getText());
        } else {
            handleMethodInvocation(ctx.name.getText());
        }
    }

    private void handlePropertyAccess(String propertyName) {
        String getterName = "get" + upperCaseFirstChar(propertyName);
        TypeContext typeContext = evaluationContext.current();
        boolean getterFound = false;
        boolean propertyFound = false;
        for (Element e : typeContext.getTypeElement().getEnclosedElements()) {
            String memberName = e.getSimpleName().toString();
            if (memberName.equals(getterName) && e.getKind() == ElementKind.METHOD && e.getModifiers().contains(Modifier.PUBLIC)) {
                getterFound = true;
                evaluationContext.forward(propertyName, TypeName.get(((ExecutableElement) e).getReturnType()));
                break;
            } else if (memberName.equals(propertyName) && e.getModifiers().contains(Modifier.PUBLIC)) {
                propertyFound = true;
                evaluationContext.forward(propertyName, TypeName.get(e.asType()));
            }
        }

        if (getterFound) {
            bodyBuilder.add("." + getterName + "()");
        } else if (propertyFound) {
            bodyBuilder.add("." + propertyName);
        } else {
            throw new ComponentResolutionException("Unable to resolve " + propertyName + " with a public getter method or field on " + typeContext.getTypeElement().getSimpleName());
        }
    }

    private void handleMethodInvocation(String methodName) {
        bodyBuilder.add("." + methodName);
        TypeContext typeContext = evaluationContext.current();
        TypeElement typeElement = typeContext.getTypeElement();
        ExecutableElement methodElement = typeElement.getEnclosedElements().stream()
                .filter(e -> e.getSimpleName().toString().equals(methodName))
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .map(e -> ((ExecutableElement) e))
                .findFirst()
                .orElseThrow(() -> new ComponentResolutionException("Unable to find method " + methodName + " on " + typeElement.getSimpleName()));
        if (methodElement.getReturnType().getKind() == TypeKind.VOID) {
            throw new ComponentResolutionException("Expression resolves to a method " + methodName + " that returns a void return value");
        }
        evaluationContext.forward(methodName, ClassName.get(methodElement.getReturnType()));
    }

    @Override
    public void enterArguments(AkimboExpressionsParser.ArgumentsContext ctx) {
        bodyBuilder.add("(");
    }

    @Override
    public void exitArguments(AkimboExpressionsParser.ArgumentsContext ctx) {
        bodyBuilder.add(")");
    }

    @Override
    public void enterArgumentList(AkimboExpressionsParser.ArgumentListContext ctx) {
        evaluationContext.expectArgumentCapture(ctx.expression().size());
    }

    @Override
    public void exitArgumentList(AkimboExpressionsParser.ArgumentListContext ctx) {
        evaluationContext.resolveArgumentCapture();
    }

    @Override
    public void enterPropertyValue(AkimboExpressionsParser.PropertyValueContext ctx) {
        String propertyKey = ctx.getText();
        propertyKey = propertyKey.substring(2, propertyKey.length() - 1);
        bodyBuilder.add(evaluationContext.property(propertyKey).getVariableName());
    }

    @Override
    public void enterNullValue(AkimboExpressionsParser.NullValueContext ctx) {
        if (evaluationContext.isPrimary()) {
            throw new ComponentResolutionException("");
        }
        bodyBuilder.add("null");
    }

    @Override
    public void enterStringValue(AkimboExpressionsParser.StringValueContext ctx) {
        String text = ctx.getText();
        text = text.substring(1, text.length() - 1);
        bodyBuilder.add("$S", text);
        evaluationContext.literal(String.class);
    }

    @Override
    public void enterNumberValue(AkimboExpressionsParser.NumberValueContext ctx) {
        bodyBuilder.add(ctx.getText());
        // todo resolve type bw Integer, Long, Double, BigDecimal, etc
        evaluationContext.literal(Integer.class);
    }

    @Override
    public void enterBooleanValue(AkimboExpressionsParser.BooleanValueContext ctx) {
        bodyBuilder.add(ctx.getText());
        evaluationContext.literal(Boolean.class);
    }

    @Override
    public ComponentProvisionResolution buildValue() {
        String resolvedComponentName = evaluationContext.getResolvedComponentName();
        return new ComponentProvisionResolution(
                resolvedComponentName,
                evaluationContext.current().getTypeName(),
                MethodSpec.methodBuilder("provides" + upperCaseFirstChar(resolvedComponentName))
                        .addAnnotation(Provides.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameters(evaluationContext.getParameterSpecs())
                        .addCode(CodeBlock.builder()
                                .add(new EnvironmentValueAssignments(evaluationContext.getPropertyValues()).toSpec())
                                .add(bodyBuilder.add(";").build())
                                .build())
                        .returns(evaluationContext.current().getTypeName())
                        .build(),
                evaluationContext.getPropertyValues()
        );
    }

}

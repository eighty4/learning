package eighty4.akimbo.compile.resolution.expression.visitor.references;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.expressions.AkimboExpressionValueListener;
import eighty4.akimbo.expressions.AkimboExpressionsParser;

import java.util.HashSet;
import java.util.Set;

public class ComponentReferenceCollector extends AkimboExpressionValueListener<ExpressionReferences> {

    private final ProcessorContext processorContext;

    private final Set<String> componentReferences = new HashSet<>();

    private final Set<ClassName> typeReferences = new HashSet<>();

    public ComponentReferenceCollector(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    @Override
    public void enterComponentReference(AkimboExpressionsParser.ComponentReferenceContext ctx) {
        componentReferences.add(ctx.name.getText());
    }

    @Override
    public void enterAkimboTypeReference(AkimboExpressionsParser.AkimboTypeReferenceContext ctx) {
        addTypeReference(processorContext.getAkimboSourcesPackageName() + "." + ctx.name.getText());
    }

    @Override
    public void enterUserTypeReference(AkimboExpressionsParser.UserTypeReferenceContext ctx) {
        addTypeReference(ctx.name.getText());
    }

    private void addTypeReference(String typeFqn) {
        typeReferences.add(ClassName.bestGuess(typeFqn));
    }

    @Override
    public ExpressionReferences buildValue() {
        return new ExpressionReferences(componentReferences, typeReferences);
    }

}

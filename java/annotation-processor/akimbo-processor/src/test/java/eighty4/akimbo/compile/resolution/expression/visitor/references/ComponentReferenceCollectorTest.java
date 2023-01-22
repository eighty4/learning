package eighty4.akimbo.compile.resolution.expression.visitor.references;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.resolution.expression.visitor.references.ComponentReferenceCollector;
import eighty4.akimbo.compile.resolution.expression.visitor.references.ExpressionReferences;
import eighty4.akimbo.compile.util.TestTypes;
import eighty4.akimbo.expressions.AkimboExpression;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComponentReferenceCollectorTest {

    @Mock
    private ProcessorContext processorContext;

    private ComponentReferenceCollector componentReferenceCollector;

    @Before
    public void setUp() {
        componentReferenceCollector = new ComponentReferenceCollector(processorContext);
    }

    @Test
    public void akimboTypeReference_newInstance() {
        when(processorContext.getAkimboSourcesPackageName()).thenReturn("eighty4.akimbo");
        ExpressionReferences references = evaluate("new @@compile.util.TestTypes.DataApi()");
        assertThat(references.getComponentReferences()).containsExactly();
        assertThat(references.getTypeReferences()).containsExactly(ClassName.get(TestTypes.DataApi.class));
    }

    static class TypeWithStaticMethod {
        static void asdf() {
        }
    }

    @Test
    public void akimboTypeReference_staticMethod() {
        when(processorContext.getAkimboSourcesPackageName()).thenReturn("eighty4.akimbo.compile.resolution");
        ExpressionReferences references = evaluate("new @@expression.visitor.references.ComponentReferenceCollectorTest.TypeWithStaticMethod()");
        assertThat(references.getComponentReferences()).containsExactly();
        assertThat(references.getTypeReferences()).containsExactly(ClassName.get(TypeWithStaticMethod.class));
    }

    @Test
    public void userTypeReference_newInstance() {
        ExpressionReferences references = evaluate("new @java.util.HashMap()");
        assertThat(references.getComponentReferences()).isEmpty();
        assertThat(references.getTypeReferences()).containsExactly(ClassName.get(HashMap.class));
    }

    @Test
    public void userTypeReference_staticMethod() {
        ExpressionReferences references = evaluate("@java.util.List.of()");
        assertThat(references.getComponentReferences()).isEmpty();
        assertThat(references.getTypeReferences()).containsExactly(ClassName.get(List.class));
    }

    @Test
    public void componentReference() {
        ExpressionReferences references = evaluate("#componentName.property");
        assertThat(references.getComponentReferences()).containsExactly("componentName");
        assertThat(references.getTypeReferences()).isEmpty();
    }

    private ExpressionReferences evaluate(String expressionString) {
        AkimboExpression expression = AkimboExpression.fromString(expressionString);
        return expression.evaluate(componentReferenceCollector);
    }

}

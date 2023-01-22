package eighty4.akimbo.compile.resolution.expression.visitor.provides;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.environment.PropertyValue;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.resolution.ComponentResolutionException;
import eighty4.akimbo.compile.resolution.TypeRegistry;
import eighty4.akimbo.compile.util.CompilationTest;
import eighty4.akimbo.compile.util.TestTypes;
import eighty4.akimbo.expressions.AkimboExpression;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Set;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComponentProvisionResolverTest extends CompilationTest {

    @Mock
    private ProcessorContext processorContext;

    @Mock
    private TypeRegistry typeRegistry;

    @Mock
    private ComponentRegistry componentRegistry;

    @Before
    public void setUp() {
        when(processorContext.getTypeRegistry()).thenReturn(typeRegistry);
        when(processorContext.getComponentRegistry()).thenReturn(componentRegistry);
    }

    @Test(expected = ComponentResolutionException.class)
    public void nullValue() {
        evaluate("null");
    }

    @Test
    public void stringValue_withName() {
        String expression = "'i will be a string'";
        String expectedCode = "return \"i will be a string\";";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "componentName", true, expectedType, expectedCode);
    }

    @Test(expected = ComponentResolutionException.class)
    public void stringValue_withoutName() {
        evaluate("'i should error bc i have no name'");
    }

    @Test
    public void intValue_WithName() {
        String expression = "1234";
        String expectedCode = "return 1234;";
        TypeName expectedType = TypeName.get(Integer.class);
        evaluateAndTest(expression, "componentName", true, expectedType, expectedCode);
    }

    @Test(expected = ComponentResolutionException.class)
    public void intValue_withoutName() {
        evaluate("1234");
    }

    @Test
    public void booleanValue_WithName() {
        String expression = "true";
        String expectedCode = "return true;";
        TypeName expectedType = TypeName.get(Boolean.class);
        evaluateAndTest(expression, "componentName", true, expectedType, expectedCode);
    }

    @Test
    public void propertyValue_withName() {
        String expression = "${foo.bar}";
        String expectedCode = "java.lang.String fooBar = eighty4.akimbo.environment.EnvironmentAccess.getProperty(\"foo.bar\", null, java.lang.String.class);\nreturn fooBar;";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "componentName", true, expectedType, expectedCode, Set.of(
                new PropertyValue("foo.bar", ClassName.get(String.class))
        ));
    }

    @Test(expected = ComponentResolutionException.class)
    public void booleanValue_withoutName() {
        evaluate("true");
    }

    static class DefaultNoArgConstructor {
        public String method() {
            return null;
        }
    }

    @Test
    public void userTypeRefInstance_noArgConstructor() {
        when(typeRegistry.getTypeElement(any())).thenReturn(getTypeElement(DefaultNoArgConstructor.class));

        String expression = "new @" + DefaultNoArgConstructor.class.getCanonicalName() + "()";
        String expectedCode = "return new " + DefaultNoArgConstructor.class.getCanonicalName() + "();";
        ClassName expectedType = ClassName.get(DefaultNoArgConstructor.class);
        evaluateAndTest(expression, "defaultNoArgConstructor", expectedType, expectedCode);

        verify(typeRegistry).getTypeElement(expectedType);
    }

    @Test
    public void akimboTypeRefInstance_noArgConstructor() {
        when(typeRegistry.getTypeElement(any())).thenReturn(getTypeElement(DefaultNoArgConstructor.class));
        when(processorContext.getAkimboSourcesPackageName()).thenReturn("eighty4.akimbo.compile.resolution.expression");

        String expression = "new @@visitor.provides.ComponentProvisionResolverTest.DefaultNoArgConstructor()";
        String expectedCode = "return new " + DefaultNoArgConstructor.class.getCanonicalName() + "();";
        ClassName expectedType = ClassName.get(DefaultNoArgConstructor.class);
        evaluateAndTest(expression, "defaultNoArgConstructor", expectedType, expectedCode);

        verify(processorContext).getAkimboSourcesPackageName();
        verify(typeRegistry).getTypeElement(expectedType);
    }

    @Test
    public void akimboTypeRefInstance_methodCall() {
        when(typeRegistry.getTypeElement(any())).thenReturn(getTypeElement(DefaultNoArgConstructor.class));
        when(processorContext.getAkimboSourcesPackageName()).thenReturn("eighty4.akimbo.compile.resolution.expression");

        String expression = "new @@visitor.provides.ComponentProvisionResolverTest.DefaultNoArgConstructor().method()";
        String expectedCode = "return new " + DefaultNoArgConstructor.class.getCanonicalName() + "().method();";
        ClassName expectedType = ClassName.get(String.class);
        evaluateAndTest(expression, "method", expectedType, expectedCode);

        verify(processorContext).getAkimboSourcesPackageName();
        verify(typeRegistry).getTypeElement(ClassName.get(DefaultNoArgConstructor.class));
    }

    static class TypeWithStatics {
        public static final String CONSTANT = "woot";

        private TypeWithStatics() {}

        public static String doSomething(String withSomeData) {
            return "";
        }
    }

    @Test
    public void userTypeRef_staticMethod() {
        when(typeRegistry.getTypeElement(any())).thenReturn(getTypeElement(TypeWithStatics.class));

        String expression = String.format("@%s.doSomething('asdf')", TypeWithStatics.class.getCanonicalName());
        String expectedCode = "return eighty4.akimbo.compile.resolution.expression.visitor.provides.ComponentProvisionResolverTest.TypeWithStatics.doSomething(\"asdf\");";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "doSomething", expectedType, expectedCode);

        verifyNoInteractions(componentRegistry);
        verify(typeRegistry).getTypeElement(ClassName.get(TypeWithStatics.class));
    }

    @Test
    @Ignore("tokenizes static field as part of type name")
    public void userTypeRef_staticField() {
        when(typeRegistry.getTypeElement(any())).thenReturn(getTypeElement(TypeWithStatics.class));

        String expression = String.format("@%s.CONSTANT", TypeWithStatics.class.getCanonicalName());
        String expectedCode = "return eighty4.akimbo.compile.util.ComponentProvisionResolverTest.TypeWithStaticCall.PUBLIC_CONSTANT;";
        ClassName expectedType = ClassName.get(String.class);
        evaluateAndTest(expression, "cONSTANT", expectedType, expectedCode);

        verifyNoInteractions(componentRegistry);
        verify(typeRegistry).getTypeElement(expectedType);
    }

    @Test
    public void userTypeRef_propertyGetter() {
        ClassName refTypeName = ClassName.get(TestTypes.NonServiceTypeWithDataApiField.class);
        TypeElement typeElement = getTypeElement(TestTypes.NonServiceTypeWithDataApiField.class);
        when(typeRegistry.getTypeElement(any())).thenReturn(typeElement);

        String expression = "new @" + createUserTypeReferenceIdentifier(refTypeName) + "().dataApi";
        String expectedCode = "return new eighty4.akimbo.compile.util.TestTypes.NonServiceTypeWithDataApiField().getDataApi();";
        evaluateAndTest(expression, "dataApi", TypeName.get(TestTypes.DataApi.class), expectedCode);

        verify(typeRegistry).getTypeElement(refTypeName);
    }

    static class PrivateConstructor {
        private PrivateConstructor() {}
    }

    @Test(expected = ComponentResolutionException.class)
    public void userTypeRef_privateConstructor() {
        ClassName refTypeName = ClassName.get(PrivateConstructor.class);
        TypeElement typeElement = getTypeElement(PrivateConstructor.class);
        when(typeRegistry.getTypeElement(any())).thenReturn(typeElement);

        evaluate("new @" + createUserTypeReferenceIdentifier(refTypeName) + "()");
    }

    private String createUserTypeReferenceIdentifier(ClassName refTypeName) {
        return refTypeName.reflectionName().replaceAll("\\$", ".");
    }

    static class NoArgsMethod {
        public String method() {
            return null;
        }
    }

    @Test
    public void componentMethodCall_noArgs() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(NoArgsMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#compName.method()";
        String expectedCode = "return compName.method();";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "method", expectedType, expectedCode);

        verify(componentRegistry).getDefinitionByName("compName");
    }

    static class NoReturnTypeMethod {
        public void method() {}
    }

    @Test(expected = ComponentResolutionException.class)
    public void componentMethodCall_noReturnType() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(NoReturnTypeMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        evaluate("#compName.method()");
    }

    static class SingleArgMethod {
        public String method(String asdf) {
            return null;
        }
    }

    @Test
    public void componentMethodCall_singleArg() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(SingleArgMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#compName.method('asdf')";
        String expectedCode = "return compName.method(\"asdf\");";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "method", expectedType, expectedCode);

        verify(componentRegistry).getDefinitionByName("compName");
    }

    @Test
    public void componentMethodCall_propertyArg() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(SingleArgMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#compName.method(${my.property})";
        String expectedCode = "java.lang.String myProperty = eighty4.akimbo.environment.EnvironmentAccess.getProperty(\"my.property\", null, java.lang.String.class);\nreturn compName.method(myProperty);";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "method", expectedType, expectedCode, Set.of(
                new PropertyValue("my.property", ClassName.get(String.class))
        ));

        verify(componentRegistry).getDefinitionByName("compName");
    }

    @Test(expected = ComponentResolutionException.class)
    public void componentMethodCall_methodDoesNotExist() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(SingleArgMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        evaluate("#compName.notMethod(${my.property})");
    }

    static class NonPubicMethod {
        private void cantCallThis() {}
    }

    @Test(expected = ComponentResolutionException.class)
    public void componentMethodCall_methodIsNotPublic() {

        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(NonPubicMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        evaluate("#compName.cantCallThis()");
    }

    static class MultipleArgsMethod {
        public String method(String foo, boolean bar, int woo) {
            return null;
        }
    }

    @Test
    public void componentMethodCall_multipleArgs() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(MultipleArgsMethod.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#compName.method('asdf', false, 8)";
        String expectedCode = "return compName.method(\"asdf\", false, 8);";
        TypeName expectedType = TypeName.get(String.class);
        evaluateAndTest(expression, "method", expectedType, expectedCode);

        verify(componentRegistry).getDefinitionByName("compName");
    }

    @Test
    public void componentPropertyGetter() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(TestTypes.HttpServiceWithDependency.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#httpServiceWithDependency.dataApi";
        String expectedCode = "return httpServiceWithDependency.getDataApi();";
        evaluateAndTest(expression, "dataApi", TypeName.get(TestTypes.DataApi.class), expectedCode);

        verify(componentRegistry).getDefinitionByName("httpServiceWithDependency");
    }

    @Test
    public void componentPublicProperty() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(TestTypes.HttpServiceWithDependency.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#httpServiceWithDependency.publicProperty";
        String expectedCode = "return httpServiceWithDependency.publicProperty;";
        evaluateAndTest(expression, "publicProperty", TypeName.get(TestTypes.DataApi.class), expectedCode);

        verify(componentRegistry).getDefinitionByName("httpServiceWithDependency");
    }

    @Test(expected = ComponentResolutionException.class)
    public void componentPrivateProperty() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(TestTypes.HttpServiceWithDependency.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        String expression = "#httpServiceWithDependency.privateProperty";
        evaluate(expression);
    }

    static class NestedMethodBuilder {
        public NestedMethodBuilder(DefaultNoArgConstructor dependency) {}

        public NestedMethodResult build() {
            return null;
        }
    }

    static class NestedMethodResult {}

    @Test
    public void nestedMethodExpression() {
        ServiceAkimboComponent componentDefinition = new ServiceAkimboComponent(getTypeElement(DefaultNoArgConstructor.class));
        when(componentRegistry.getDefinitionByName(anyString())).thenReturn(componentDefinition);

        when(typeRegistry.getTypeElement(ClassName.get(NestedMethodBuilder.class))).thenReturn(getTypeElement(NestedMethodBuilder.class));
        when(processorContext.getAkimboSourcesPackageName()).thenReturn("eighty4.akimbo.compile.resolution.expression.visitor.provides");

        String expression = "new @@ComponentProvisionResolverTest.NestedMethodBuilder(#dependency).build()";
        String expectedCode = "return new eighty4.akimbo.compile.resolution.expression.visitor.provides.ComponentProvisionResolverTest.NestedMethodBuilder(dependency).build();";
        evaluateAndTest(expression, "build", ClassName.get(NestedMethodResult.class), expectedCode);

        verify(componentRegistry).getDefinitionByName("dependency");
    }

    private void evaluateAndTest(String expression, String expectedComponentName, TypeName expectedType, String expectedCode) {
        evaluateAndTest(expression, expectedComponentName, false, expectedType, expectedCode, Collections.emptySet());
    }

    private void evaluateAndTest(String expression, String expectedComponentName, TypeName expectedType, String expectedCode, Set<PropertyValue> propertyValues) {
        evaluateAndTest(expression, expectedComponentName, false, expectedType, expectedCode, propertyValues);
    }

    private void evaluateAndTest(String expression, String expectedComponentName, boolean explicitComponentName, TypeName expectedType, String expectedCode) {
        evaluateAndTest(expression, expectedComponentName, explicitComponentName, expectedType, expectedCode, Collections.emptySet());
    }

    private void evaluateAndTest(String expression, String expectedComponentName, boolean explicitComponentName, TypeName expectedType, String expectedCode, Set<PropertyValue> propertyValues) {
        ComponentProvisionResolution componentProvisionResolution = evaluate(expression, new ComponentProvisionResolver(processorContext, explicitComponentName ? expectedComponentName : null));
        assertThat(componentProvisionResolution.getResolvedName()).isEqualTo(expectedComponentName);
        assertThat(componentProvisionResolution.getResolvedType()).isEqualTo(expectedType);
        assertThat(componentProvisionResolution.getPropertyValues()).isEqualTo(propertyValues);
        MethodSpec providesMethod = componentProvisionResolution.getProvidesMethod();
        assertThat(providesMethod.code.toString()).isEqualTo(expectedCode);
        assertThat(providesMethod.returnType).isEqualTo(expectedType);
        assertThat(providesMethod.name).isEqualTo("provides" + upperCaseFirstChar(expectedComponentName));
        // todo test parameters
    }

    private ComponentProvisionResolution evaluate(String expression) {
        return evaluate(expression, new ComponentProvisionResolver(processorContext, null));
    }

    private ComponentProvisionResolution evaluate(String expression, ComponentProvisionResolver componentProvisionResolver) {
        AkimboExpression akimboExpression = AkimboExpression.fromString(expression);
        return akimboExpression.evaluate(componentProvisionResolver);
    }

}

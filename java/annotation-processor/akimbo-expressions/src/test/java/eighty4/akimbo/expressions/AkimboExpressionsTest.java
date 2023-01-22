package eighty4.akimbo.expressions;

import org.antlr.v4.runtime.Token;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class AkimboExpressionsTest {

    private static final Map<String, List<String>> PROP_VALUES = new HashMap<>();

    private static final Map<String, List<String>> LITERAL_VALUES = new HashMap<>();

    private static final Map<String, List<String>> COMPONENT_REFS = new HashMap<>();

    private static final Map<String, List<String>> TYPE_REFS = new HashMap<>();

    static {
        // property refs
        PROP_VALUES.put("${inject.a.property}", null);
        PROP_VALUES.put("${INJECT_AN_ENV_VAR}", null);

        // literal values
        LITERAL_VALUES.put("''", null);
        LITERAL_VALUES.put("'i am a string'", null);
        LITERAL_VALUES.put("1234", null);
        LITERAL_VALUES.put("true", null);
        LITERAL_VALUES.put("false", null);
        LITERAL_VALUES.put("null", null);

        // component refs
        COMPONENT_REFS.put("#componentName.property", null);
        COMPONENT_REFS.put("#componentName.nested.properties", null);
        COMPONENT_REFS.put("#componentName.methodNoArgs()", null);
        COMPONENT_REFS.put("#componentName.methodStringArg('my string arg')", null);
        COMPONENT_REFS.put("#componentName.methodIntegerArg(1234)", null);
        COMPONENT_REFS.put("#componentName.methodTrueArg(true)", null);
        COMPONENT_REFS.put("#componentName.methodFalseArg(false)", null);
        COMPONENT_REFS.put("#componentName.methodNullArg(null)", null);
        COMPONENT_REFS.put("#componentName.withPropertyArg(${this.my.property})", null);
        COMPONENT_REFS.put("#componentName.withPropertyArg(${THIS_MY_ENV_VAR})", null);
        COMPONENT_REFS.put("#componentName.methodComponentArg(#anotherComponent)", null);
        COMPONENT_REFS.put("#componentName.methodComponentPropertyArg(#anotherComponent.property)", null);
        COMPONENT_REFS.put("#componentName.withEvalArg(#anotherComponent.method())", null);
        COMPONENT_REFS.put("#componentName.withArgEvalArgs(#anotherComponent.method(true, 'crew', 1234, #anotherComponent, ${some.prop.right.here}))", null);
        COMPONENT_REFS.put("#componentName.nestedProp.nestedMethod()", null);
        COMPONENT_REFS.put("#componentName.nestedMethod().nestedProp", null);

        // user type refs - constructor calls
        TYPE_REFS.put("new @NoSubPackage()", null);
        TYPE_REFS.put("new @type.NoArgs()", null);
        TYPE_REFS.put("new @type.WithComponentRefConstructorArg(#someComponent)", null);
        TYPE_REFS.put("new @type.WithStringConstructorArg('asdf')", null);
        TYPE_REFS.put("new @type.WithNumberConstructorArg(1234)", null);
        TYPE_REFS.put("new @type.WithBooleanConstructorArg(true)", null);
        TYPE_REFS.put("new @type.WithNullConstructorArg(null)", null);
        TYPE_REFS.put("new @type.WithPropertyConstructorArg(${some.property})", null);
        TYPE_REFS.put("new @type.ResolvingToProperty().someProperty", null);
        TYPE_REFS.put("new @type.ResolvingToMethodResult().someMethodResult()", null);

        // akimbo type refs - constructor calls
        TYPE_REFS.put("new @@NoSubPackage()", null);
        TYPE_REFS.put("new @@type.NoArgs()", null);
        TYPE_REFS.put("new @@type.WithComponentRefConstructorArg(#someComponent)", null);
        TYPE_REFS.put("new @@type.WithStringConstructorArg('asdf')", null);
        TYPE_REFS.put("new @@type.WithNumberConstructorArg(1234)", null);
        TYPE_REFS.put("new @@type.WithBooleanConstructorArg(true)", null);
        TYPE_REFS.put("new @@type.WithNullConstructorArg(null)", null);
        TYPE_REFS.put("new @@type.WithPropertyConstructorArg(${some.property})", null);
        TYPE_REFS.put("new @@type.ResolvingToProperty().someProperty", null);
        TYPE_REFS.put("new @@type.ResolvingToMethodResult().someMethodResult()", null);
        TYPE_REFS.put("new @@type.TypeName(#componentArgument).someMethod()", null);

        // static api calls
        TYPE_REFS.put("@nested.package.Type.staticMethod('arg')", null);
        TYPE_REFS.put("@Type.staticMethod(1234).prop.method(null)", null);

        // valid expression, but tokenized with CONSTANT_FIELD as part of the type ref
        // todo require @{type.Ref} notation or in listener catch type resolving error, chop off end until type resolves
        TYPE_REFS.put("@Type.CONSTANT_FIELD.isEmpty()", null);
    }

    @Test
    public void testPropertyValues() {
        PROP_VALUES.forEach(this::evaluate);
    }

    @Test
    public void testLiteralValues() {
        LITERAL_VALUES.forEach(this::evaluate);
    }

    @Test
    public void testComponentRefs() {
        COMPONENT_REFS.forEach(this::evaluate);
    }

    @Test
    public void testTypeRefs() {
        TYPE_REFS.forEach(this::evaluate);
    }

    private void evaluate(String expression, List<String> tokens) {
        AkimboExpression akimboExpression = AkimboExpression.fromString(expression);
        if (tokens != null) {
            assertThat(getTokens(akimboExpression)).containsExactlyElementsOf(tokens);
        }
        try {
            akimboExpression.evaluate((new AkimboExpressionsParserBaseListener()));
        } catch (Exception e) {
            fail("Error on expression \"" + expression + "\": " + e.getMessage());
        }
    }

    private List<String> getTokens(AkimboExpression akimboExpression) {
        List<String> tokens = akimboExpression.getLexer().getAllTokens().stream().map(Token::getText).collect(Collectors.toList());
        akimboExpression.getLexer().reset();
        return tokens;
    }

}

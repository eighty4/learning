package eighty4.akimbo.compile.resolution.expression;

import com.squareup.javapoet.TypeName;

public class ResolutionExpression {

    public static ResolutionExpression of(String typeExpression, String explicitName, TypeName explicitType) {
        return new ResolutionExpression(typeExpression, explicitName, explicitType);
    }

    private final String expression;

    private final String explicitName;

    private final TypeName explicitType;

    private ResolutionExpression(String expression, String explicitName, TypeName explicitType) {
        this.expression = expression;
        this.explicitName = explicitName;
        this.explicitType = explicitType;
    }

    public String getExpression() {
        return expression;
    }

    public String getExplicitName() {
        return explicitName;
    }

    public TypeName getExplicitType() {
        return explicitType;
    }

}

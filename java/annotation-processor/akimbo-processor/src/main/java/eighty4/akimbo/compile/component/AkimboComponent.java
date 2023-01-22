package eighty4.akimbo.compile.component;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.resolution.expression.ResolutionExpression;

public class AkimboComponent {

    public static AkimboComponent of(AkimboComponentDefinition componentDefinition) {
        return new AkimboComponent(componentDefinition);
    }

    public static AkimboComponent of(ClassName type) {
        return new AkimboComponent(type);
    }

    public static AkimboComponent of(Class<?> type) {
        return new AkimboComponent(ClassName.get(type));
    }

    public static AkimboComponent of(String resolutionExpression) {
        return new AkimboComponent(resolutionExpression);
    }

    private final AkimboComponentDefinition componentDefinition;

    private final ClassName type;

    private final String resolutionExpression;

    private String explicitName;

    private TypeName explicitType;

    public AkimboComponent(AkimboComponentDefinition componentDefinition) {
        this.componentDefinition = componentDefinition;
        resolutionExpression = null;
        type = null;
    }

    private AkimboComponent(ClassName type) {
        this.type = type;
        resolutionExpression = null;
        componentDefinition = null;
    }

    private AkimboComponent(String resolutionExpression) {
        type = null;
        this.resolutionExpression = resolutionExpression;
        componentDefinition = null;
    }

    public AkimboComponent withName(String explicitName) {
        this.explicitName = explicitName.startsWith("#") ? explicitName.substring(1) : explicitName;
        return this;
    }

    public AkimboComponent asType(TypeName explicitType) {
        this.explicitType = explicitType;
        return this;
    }

    public AkimboComponentDefinition getComponentDefinition() {
        return componentDefinition;
    }

    public ClassName getType() {
        return type;
    }

    public ResolutionExpression getResolutionExpression() {
        return ResolutionExpression.of(resolutionExpression, explicitName, explicitType);
    }

}

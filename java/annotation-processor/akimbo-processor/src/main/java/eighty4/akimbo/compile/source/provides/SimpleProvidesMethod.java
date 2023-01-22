package eighty4.akimbo.compile.source.provides;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public abstract class SimpleProvidesMethod implements ProvidesMethod {

    private final TypeName type;

    private final String methodName;

    public SimpleProvidesMethod(Class<?> typeClass) {
        this(ClassName.get(typeClass));
    }

    public SimpleProvidesMethod(ClassName type) {
        this.type = type;
        methodName = "provides" + type.simpleName();
    }

    public SimpleProvidesMethod(TypeName type, String methodName) {
        this.type = type;
        this.methodName = methodName;
    }

    public abstract CodeBlock buildMethodBody();

    @Override
    public TypeName getReturnType() {
        return type;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

}

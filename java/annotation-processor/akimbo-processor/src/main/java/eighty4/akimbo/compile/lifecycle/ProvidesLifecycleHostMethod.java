package eighty4.akimbo.compile.lifecycle;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import eighty4.akimbo.compile.source.provides.SimpleProvidesMethod;
import eighty4.akimbo.lifecycle.AkimboLifecycleServiceHost;

import java.util.Collection;
import java.util.List;

public class ProvidesLifecycleHostMethod extends SimpleProvidesMethod {

    public static final String LIFECYCLE_HOOKS_PARAM_NAME = "lifecycleHooks";

    public ProvidesLifecycleHostMethod() {
        super(AkimboLifecycleServiceHost.class);
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return List.of(ParameterSpec.builder(ProvidesLifecycleHooksMethod.LIFECYCLE_HOOKS_MAP_TYPE, LIFECYCLE_HOOKS_PARAM_NAME).build());
    }

    @Override
    public CodeBlock buildMethodBody() {
        return CodeBlock.builder()
                .addStatement("return new $T($L)", getReturnType(), LIFECYCLE_HOOKS_PARAM_NAME)
                .build();
    }
}

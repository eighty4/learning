package eighty4.akimbo.compile.lifecycle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.service.ServiceRegistration;
import eighty4.akimbo.compile.source.provides.SimpleProvidesMethod;
import eighty4.akimbo.lifecycle.LifecycleHook;
import eighty4.akimbo.lifecycle.LifecyclePhase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProvidesLifecycleHooksMethod extends SimpleProvidesMethod {

    static final TypeName LIFECYCLE_HOOKS_MAP_TYPE = ParameterizedTypeName.get(
            ClassName.get(Map.class), TypeName.get(LifecyclePhase.class),
            ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(LifecycleHook.class))
    );

    private final Collection<LifecycleServiceRegistration> lifecycleServiceRegistrations;

    public ProvidesLifecycleHooksMethod(Collection<LifecycleServiceRegistration> lifecycleServiceRegistrations) {
        super(LIFECYCLE_HOOKS_MAP_TYPE, "providesLifecycleHooks");
        this.lifecycleServiceRegistrations = lifecycleServiceRegistrations;
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return lifecycleServiceRegistrations.stream()
                .map(ServiceRegistration::getComponent)
                .map(component -> ParameterSpec.builder(component.getType(), component.getName()).build())
                .collect(Collectors.toList());
    }

    @Override
    public CodeBlock buildMethodBody() {
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T<$T> startupHooks = new $T<>()", List.class, LifecycleHook.class, ArrayList.class)
                .addStatement("$T<$T> shutdownHooks = new $T<>()", List.class, LifecycleHook.class, ArrayList.class);
        lifecycleServiceRegistrations.forEach(lifecycleServiceRegistration -> lifecycleServiceRegistration.getServiceMethodRegistrations().forEach(lifecycleHook -> {
            lifecycleHook.getLifecyclePhases().forEach(lifecyclePhase -> {
                String collectionVariable = lifecyclePhase.toString().toLowerCase() + "Hooks";
                body.addStatement("$L.add(new $T($S, $S, $L::$L))",
                        collectionVariable, LifecycleHook.class, lifecycleServiceRegistration.getComponentName(),
                        lifecycleHook.getMethodName(), lifecycleServiceRegistration.getComponentName(), lifecycleHook.getMethodName()
                );
            });
        }));
        body.addStatement("return $T.of($T.STARTUP, startupHooks, LifecyclePhase.SHUTDOWN, shutdownHooks)", Map.class, LifecyclePhase.class);
        return body.build();
    }

}

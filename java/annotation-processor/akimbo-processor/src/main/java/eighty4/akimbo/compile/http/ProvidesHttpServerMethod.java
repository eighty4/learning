package eighty4.akimbo.compile.http;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import eighty4.akimbo.compile.source.provides.SimpleProvidesMethod;
import eighty4.akimbo.environment.EnvironmentAccess;
import eighty4.akimbo.http.HttpServiceProvider;

import java.util.Collection;
import java.util.List;

import static eighty4.akimbo.compile.http.ProvidesHttpHandlersMethod.HTTP_HANDLERS_TYPE;

public class ProvidesHttpServerMethod extends SimpleProvidesMethod {

    public static final String HTTP_HANDLERS_PARAM_NAME = "httpHandlers";

    public ProvidesHttpServerMethod() {
        super(HttpServiceProvider.class);
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return List.of(ParameterSpec.builder(HTTP_HANDLERS_TYPE, HTTP_HANDLERS_PARAM_NAME).build());
    }

    @Override
    public CodeBlock buildMethodBody() {
        return CodeBlock.builder()
                .addStatement("int httpPort = $T.getProperty($S, $S, Integer.class)", EnvironmentAccess.class, "akimbo.http.port", "8080")
                .addStatement("return new $T(httpPort, $L)", getReturnType(), HTTP_HANDLERS_PARAM_NAME)
                .build();
    }
}

package eighty4.akimbo.compile.http;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import eighty4.akimbo.compile.source.provides.SimpleProvidesMethod;
import eighty4.akimbo.http.AkimboHttpServiceHost;
import eighty4.akimbo.http.HttpServiceProvider;

import java.util.Collection;
import java.util.List;

public class ProvidesHttpHostMethod extends SimpleProvidesMethod {

    public static final String HTTP_HANDLERS_PARAM_NAME = "httpServiceProvider";

    public ProvidesHttpHostMethod() {
        super(AkimboHttpServiceHost.class);
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return List.of(ParameterSpec.builder(HttpServiceProvider.class, HTTP_HANDLERS_PARAM_NAME).build());
    }

    @Override
    public CodeBlock buildMethodBody() {
        return CodeBlock.builder()
                .addStatement("return new $T($L)", getReturnType(), HTTP_HANDLERS_PARAM_NAME)
                .build();
    }
}

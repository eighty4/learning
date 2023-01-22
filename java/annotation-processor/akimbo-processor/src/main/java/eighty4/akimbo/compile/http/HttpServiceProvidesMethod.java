package eighty4.akimbo.compile.http;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;
import eighty4.akimbo.http.HttpHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

public class HttpServiceProvidesMethod implements ProvidesMethod {

    private final HttpServiceRegistration httpServiceRegistration;

    public HttpServiceProvidesMethod(HttpServiceRegistration httpServiceRegistration) {
        this.httpServiceRegistration = httpServiceRegistration;
    }

    @Override
    public Optional<String> getQualifier() {
        return Optional.of(httpServiceRegistration.getHttpHandlersQualifierName());
    }

    @Override
    public TypeName getReturnType() {
        return ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(ClassName.get(HttpHandler.class), WildcardTypeName.subtypeOf(Object.class)));
    }

    @Override
    public String getMethodName() {
        return "provides" + httpServiceRegistration.getHttpHandlersQualifierName();
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return List.of(ParameterSpec.builder(httpServiceRegistration.getComponent().getType(), "component").build());
    }

    @Override
    public CodeBlock buildMethodBody() {
        CodeBlock.Builder body = CodeBlock.builder();
        List<String> handlerNames = new ArrayList<>();
        httpServiceRegistration.getServiceMethodRegistrations().forEach(httpHandlerRegistration -> {
            String componentName = lowerCaseFirstChar(httpHandlerRegistration.getName());
            handlerNames.add(componentName);
            body.addStatement("$T<$T> $L = new $T.$L(component)",
                    HttpHandler.class,
                    httpServiceRegistration.getComponent().getType(),
                    componentName,
                    httpServiceRegistration.getGeneratedHttpServiceType(),
                    httpHandlerRegistration.getName()
            );
        });
        body.addStatement("return $T.of(\n$L\n)", List.class, String.join(",\n", handlerNames));
        return body.build();
    }
}

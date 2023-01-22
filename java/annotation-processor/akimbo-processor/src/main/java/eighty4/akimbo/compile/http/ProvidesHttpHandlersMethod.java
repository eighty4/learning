package eighty4.akimbo.compile.http;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import eighty4.akimbo.compile.source.provides.SimpleProvidesMethod;
import eighty4.akimbo.http.HttpHandler;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProvidesHttpHandlersMethod extends SimpleProvidesMethod {

    static final TypeName HTTP_HANDLERS_TYPE = ParameterizedTypeName.get(
            ClassName.get(List.class),
            ParameterizedTypeName.get(ClassName.get(HttpHandler.class), WildcardTypeName.subtypeOf(Object.class))
    );

    private final Collection<HttpServiceRegistration> httpServiceRegistrations;

    public ProvidesHttpHandlersMethod(Collection<HttpServiceRegistration> httpServiceRegistrations) {
        super(HTTP_HANDLERS_TYPE, "providesHttpHandlers");
        this.httpServiceRegistrations = httpServiceRegistrations;
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return httpServiceRegistrations.stream()
                .map(httpServiceRegistration -> ParameterSpec.builder(HTTP_HANDLERS_TYPE, httpServiceRegistration.getHttpHandlersQualifierName())
                        .addAnnotation(AnnotationSpec.builder(Named.class).addMember("value", "$S", httpServiceRegistration.getHttpHandlersQualifierName()).build())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CodeBlock buildMethodBody() {
        List<String> httpHandlerQualifiers = httpServiceRegistrations.stream()
                .map(HttpServiceRegistration::getHttpHandlersQualifierName)
                .collect(Collectors.toList());
        return CodeBlock.builder()
                .addStatement("$T<$T<?>> httpHandlers = $T.of($L).flatMap($T::stream).collect($T.toList())",
                        List.class, HttpHandler.class, Stream.class, String.join(", ", httpHandlerQualifiers), Collection.class, Collectors.class)
                .addStatement("return new $T<>(httpHandlers)", ArrayList.class)
                .build();
    }
}

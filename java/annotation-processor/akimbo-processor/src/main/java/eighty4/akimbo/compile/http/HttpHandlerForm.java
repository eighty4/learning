package eighty4.akimbo.compile.http;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.JsonUtil;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.source.SourceForm;
import eighty4.akimbo.http.HttpHandler;
import eighty4.akimbo.http.HttpMethod;
import eighty4.akimbo.http.ResponseHeaders;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static eighty4.akimbo.compile.http.HttpHandlerReturnType.MONO_VOID;
import static eighty4.akimbo.compile.http.HttpHandlerReturnType.VALUE;

public class HttpHandlerForm implements SourceForm<TypeSpec> {

    private final HttpHandlerRegistration httpHandlerRegistration;

    HttpHandlerForm(HttpHandlerRegistration httpHandlerRegistration) {
        this.httpHandlerRegistration = httpHandlerRegistration;
    }

    @Override
    public TypeSpec toSpec() {
        return TypeSpec.classBuilder(httpHandlerRegistration.getName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(HttpHandler.class), httpHandlerRegistration.getComponentType()))
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(httpHandlerRegistration.getComponentType(), "httpService")
                        .addStatement("super($T.$L, $S, httpService)", HttpMethod.class, httpHandlerRegistration.getMethod(), httpHandlerRegistration.getPath())
                        .build())
                .addMethod(MethodSpec.methodBuilder("apply")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(HttpServerRequest.class, "request")
                        .addParameter(HttpServerResponse.class, "response")
                        .addCode(buildHandlerMethodCodeBlock())
                        .returns(ParameterizedTypeName.get(Publisher.class, Void.class))
                        .build())
                .build();
    }

    private CodeBlock buildHandlerMethodCodeBlock() {
        CodeBlock.Builder handlerMethod = CodeBlock.builder();
        getParamCodeBlocks().forEach(handlerMethod::add);

        handlerMethod.add(httpHandlerRegistration.getRequestBodyParameter().isPresent()
                ? buildInvocationAndSendResponseWithRequestParamCodeBlock(httpHandlerRegistration.getRequestBodyParameter().get())
                : buildInvocationAndSendResponseCodeBlock());

        return handlerMethod.build();
    }

    private List<CodeBlock> getParamCodeBlocks() {
        List<CodeBlock> paramCodeBlocks = new ArrayList<>();
        httpHandlerRegistration.getResponseHeadersParameter()
                .map(responseHeaderRegistration -> CodeBlock.builder().addStatement("$T $L = new $T()",
                        ResponseHeaders.class,
                        responseHeaderRegistration.getName(),
                        ResponseHeaders.class)
                        .build())
                .ifPresent(paramCodeBlocks::add);
        httpHandlerRegistration.getRequestParameters().stream()
                .map(RequestParameterForm::new)
                .map(SourceForm::toSpec)
                .forEach(paramCodeBlocks::add);
        return paramCodeBlocks;
    }

    private CodeBlock buildInvocationAndSendResponseWithRequestParamCodeBlock(Parameter requestBodyParameter) {
        return CodeBlock.builder()
                .add("return request.receive().aggregate().asString()\n.map(json -> $T.fromJson(json, $T.class))\n.flatMap($L -> {\n",
                        JsonUtil.class, requestBodyParameter.getType(), requestBodyParameter.getName())
                .add("$L", buildInvocationAndSendResponseCodeBlock())
                .add("});\n")
                .build();
    }

    private CodeBlock buildInvocationAndSendResponseCodeBlock() {
        CodeBlock invocationCodeBlock = CodeBlock.builder()
                .add("getComponent().$L($L)", httpHandlerRegistration.getMethodName(), httpHandlerRegistration.getParameterNames())
                .build();

        if (httpHandlerRegistration.getHttpHandlerReturnType() == VALUE) {
            invocationCodeBlock = CodeBlock.builder().add("$T.just($L)", Mono.class, invocationCodeBlock).build();
        }

        switch (httpHandlerRegistration.getHttpHandlerReturnType()) {
            case VALUE:
            case MONO_VALUE:
            case MONO_VOID:
                return buildSendResponseCodeBlock(invocationCodeBlock);
            case VOID:
                return CodeBlock.builder()
                        .addStatement(invocationCodeBlock)
                        .add(buildSendResponseCodeBlock())
                        .build();
            default:
                throw new IllegalStateException("wtf");
        }
    }

    private CodeBlock buildSendResponseCodeBlock(CodeBlock invocationChain) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add(invocationChain);
        httpHandlerRegistration.getResponseHeadersParameter()
                .ifPresent(parameter -> builder.add("\n.map(ir -> { response.headers($L); return ir; })", parameter.getName()));
        if (httpHandlerRegistration.getHttpHandlerReturnType() == MONO_VOID) {
            builder.add("\n.flatMap(noData -> response.status($L).send())", httpHandlerRegistration.getSuccessfulResponseStatusCode());
        } else {
            builder.add("\n.map($T::toJson)", JsonUtil.class);
            builder.add("\n.flatMap(json -> response.status($L).sendString($T.just(json)).then())",
                    httpHandlerRegistration.getSuccessfulResponseStatusCode(), Mono.class);
        }

        return CodeBlock.builder()
                .addStatement("return $L", builder.build())
                .build();
    }

    private CodeBlock buildSendResponseCodeBlock() {
        CodeBlock.Builder builder = CodeBlock.builder();
        httpHandlerRegistration.getResponseHeadersParameter()
                .ifPresent(parameter -> builder.addStatement("response.headers($L)", parameter.getName()));
        builder.addStatement("return response.status($L).send()", httpHandlerRegistration.getSuccessfulResponseStatusCode());
        return builder.build();
    }
}

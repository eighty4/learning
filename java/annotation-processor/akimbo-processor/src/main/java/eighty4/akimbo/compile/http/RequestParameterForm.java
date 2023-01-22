package eighty4.akimbo.compile.http;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.ValueUtil;
import eighty4.akimbo.annotation.http.RequestHeader;
import eighty4.akimbo.compile.source.SourceForm;

import java.util.ArrayList;
import java.util.List;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.getNameFromTypeName;

public class RequestParameterForm implements SourceForm<CodeBlock> {
    private final RequestParameter requestParameter;

    RequestParameterForm(RequestParameter requestParameter) {
        this.requestParameter = requestParameter;
    }

    // todo handle primitive types
    @Override
    public CodeBlock toSpec() {
        StringBuilder expression = new StringBuilder("$T $L = ");
        List<Object> args = new ArrayList<>();
        args.add(requestParameter.getType());
        args.add(requestParameter.getName());
        int closeFnCalls = 0;

        if (requestParameter.isRequired()) {
            closeFnCalls++;
            expression.append("$T.required(");
            args.add(ValueUtil.class);
        }

        if (requestParameter.getDefaultValue() != null) {
            closeFnCalls++;
            expression.append("$T.defaultValue($S, ");
            args.add(ValueUtil.class);
            args.add(requestParameter.getDefaultValue());
        }

        if (!isStringType(requestParameter.getType())) {
            closeFnCalls++;
            expression.append("$T.$L(");
            args.add(ValueUtil.class);
            args.add("as" + getNameFromTypeName(requestParameter.getType()));
        }

        if (requestParameter.getAnnotationClass() == RequestHeader.class) {
            expression.append("request.requestHeaders().get");
        } else {
            expression.append("request.param");
        }
        expression.append("($S)");
        args.add(requestParameter.getRequestParameterName());

        expression.append(")".repeat(Math.max(0, closeFnCalls)));

        return CodeBlock.builder()
                .addStatement(expression.toString(), args.toArray())
                .build();
    }

    private boolean isStringType(TypeName type) {
        return type.equals(TypeName.get(String.class));
    }
}

package eighty4.akimbo.compile.http;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import reactor.core.publisher.Mono;

public enum HttpHandlerReturnType {
    VOID, MONO_VOID, VALUE, MONO_VALUE;

    public static HttpHandlerReturnType fromTypeName(TypeName returnType) {
        if (TypeName.VOID.equals(returnType)) {
            return VOID;
        }
        if (ParameterizedTypeName.get(ClassName.get(Mono.class), TypeName.VOID.box()).equals(returnType)) {
            return MONO_VOID;
        }
        if (returnType.getClass().isAssignableFrom(ParameterizedTypeName.class)
                && ((ParameterizedTypeName) returnType).rawType.compareTo(ClassName.get(Mono.class)) == 0) {
            return MONO_VALUE;
        }
        return VALUE;
    }
}

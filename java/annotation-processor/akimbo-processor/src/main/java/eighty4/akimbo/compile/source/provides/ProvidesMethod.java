package eighty4.akimbo.compile.source.provides;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import dagger.Provides;
import eighty4.akimbo.compile.source.SourceForm;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface ProvidesMethod extends SourceForm<MethodSpec> {

    default MethodSpec toSpec() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(getMethodName());
        builder.addAnnotation(Singleton.class);
        getQualifier().ifPresent(qualifier -> builder.addAnnotation(buildNamedAnnotation(qualifier)));
        builder.addAnnotation(Provides.class);
        builder.addParameters(buildMethodParameters());
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.returns(getReturnType());
        builder.addCode(buildMethodBody());
        getNamedAnnotation().ifPresent(builder::addAnnotation);
        return builder.build();
    }

    default Optional<AnnotationSpec> getNamedAnnotation() {
        return Optional.empty();
    }

    default Optional<String> getQualifier() {
        return Optional.empty();
    }

    String getMethodName();

    TypeName getReturnType();

    default Collection<ParameterSpec> buildMethodParameters() {
        return Collections.emptySet();
    }

    CodeBlock buildMethodBody();

    default AnnotationSpec buildNamedAnnotation(String qualifier) {
        return AnnotationSpec.builder(Named.class)
                .addMember("value", "$S", qualifier)
                .build();
    }

}

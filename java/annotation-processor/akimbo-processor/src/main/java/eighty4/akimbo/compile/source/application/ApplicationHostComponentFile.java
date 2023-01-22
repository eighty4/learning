package eighty4.akimbo.compile.source.application;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import dagger.Component;
import eighty4.akimbo.AkimboApplicationHost;
import eighty4.akimbo.compile.source.SourceFile;

import javax.inject.Singleton;
import javax.lang.model.element.Modifier;

public class ApplicationHostComponentFile extends SourceFile {

    public ApplicationHostComponentFile(String packageName) {
        super(packageName, "AkimboApplicationHost_Component");
    }

    @Override
    public TypeSpec toSpec() {
        return TypeSpec.interfaceBuilder(getType())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Component.class)
                        .addMember("modules", "$T.class", ClassName.bestGuess(getPackageName() + ".AkimboApplicationHost_Module"))
                        .build())
                .addAnnotation(Singleton.class)
                .addMethod(MethodSpec.methodBuilder("akimboApplicationHost")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(AkimboApplicationHost.class)
                        .build())
                .build();
    }
}

package eighty4.akimbo.compile.source.application;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.Modifier;

public class AkimboApplicationMainFile extends SourceFile {

    public AkimboApplicationMainFile(String packageName) {
        super(packageName, "AkimboApplicationMain");
    }

    @Override
    public TypeSpec toSpec() {
        return TypeSpec.classBuilder(getType())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(String[].class, "args")
                        .addStatement("$T.create().akimboApplicationHost().start()", ClassName.bestGuess(getPackageName() + ".DaggerAkimboApplicationHost_Component"))
                        .returns(void.class)
                        .build())
                .build();
    }
}

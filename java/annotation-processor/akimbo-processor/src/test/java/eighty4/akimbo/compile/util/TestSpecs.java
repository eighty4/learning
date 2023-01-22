package eighty4.akimbo.compile.util;

import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.annotation.AkimboApp;

import javax.lang.model.element.Modifier;

public abstract class TestSpecs implements TestModels {

    public static final TypeSpec APP = TypeSpec.classBuilder("TestApp")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(AkimboApp.class)
            .build();
}

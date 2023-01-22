package eighty4.akimbo.compile.source;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.compile.AkimboProcessor;
import eighty4.akimbo.compile.ProcessorContext;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Generated;
import java.io.IOException;

public abstract class SourceFile implements SourceForm<TypeSpec> {

    private final ClassName type;

    public SourceFile(String packageName, String name) {
        this(ClassName.get(packageName, name));
    }

    public SourceFile(ClassName type) {
        this.type = type;
    }

    public ClassName getType() {
        return type;
    }

    public String getPackageName() {
        return type.packageName();
    }

    public void writeTo(ProcessorContext processorContext, Filer filer) {
        try {
            toJavaFile(processorContext).writeTo(filer);
        } catch (IOException e) {
            throw new IllegalStateException("failed to write sources", e);
        }
    }

    private JavaFile toJavaFile(ProcessorContext processorContext) {
        String packageName = getPackageName();
        if (packageName.startsWith("@@")) {
            packageName = processorContext.getAkimboSourcesPackageName() + "." + packageName.substring(2);
        } else if (packageName.startsWith("@")) {
            packageName = processorContext.getAkimboSourcesPackageName() + "." + packageName.substring(1);
        }
        return JavaFile.builder(packageName, toSpec().toBuilder().addAnnotation(buildGeneratedAnnotation()).build()).build();
    }

    private AnnotationSpec buildGeneratedAnnotation() {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", AkimboProcessor.class.getCanonicalName())
                .addMember("comments", "$S", "github.com/eighty4/akimbo")
                .build();
    }

    @Override
    public String toString() {
        return type.reflectionName();
    }
}

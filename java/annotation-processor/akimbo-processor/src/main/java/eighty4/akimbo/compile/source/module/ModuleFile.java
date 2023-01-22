package eighty4.akimbo.compile.source.module;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import dagger.Module;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ModuleFile extends SourceFile {

    public ModuleFile(ClassName moduleType) {
        super(moduleType);
    }

    public ModuleFile(String packageName, String name) {
        super(packageName, name);
    }

    @Override
    public TypeSpec toSpec() {
        return TypeSpec.interfaceBuilder(getType())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(buildDaggerModuleAnnotation())
                .addMethods(getProvidesMethods())
                .build();
    }

    private AnnotationSpec buildDaggerModuleAnnotation() {
        Set<ClassName> moduleIncludes = getModuleDependencies();
        if (moduleIncludes.isEmpty()) {
            return AnnotationSpec.builder(Module.class).build();
        } else {
            String template = "{" + moduleIncludes.stream().map(mi -> "$T.class").collect(Collectors.joining(", ")) + "}";
            return AnnotationSpec.builder(Module.class)
                    .addMember("includes", template, moduleIncludes.toArray())
                    .build();
        }
    }

    protected abstract Set<ClassName> getModuleDependencies();

    protected abstract Collection<MethodSpec> getProvidesMethods();

}

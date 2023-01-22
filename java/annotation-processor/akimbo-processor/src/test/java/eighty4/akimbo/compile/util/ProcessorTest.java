package eighty4.akimbo.compile.util;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import dagger.internal.codegen.ComponentProcessor;
import eighty4.akimbo.compile.AkimboProcessor;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessorTest {

    protected Compilation compileFiles(Collection<Processor> additionalProcessors, Collection<JavaFileObject> javaFileObjects) {
        List<Processor> processorsToUse = new ArrayList<>(List.of(new AkimboProcessor(), new ComponentProcessor()));
        processorsToUse.addAll(additionalProcessors);
        return Compiler.javac()
                .withProcessors(processorsToUse)
                .compile(javaFileObjects);
    }

    protected Compilation compileFiles(Collection<Processor> additionalProcessors, String packageName, TypeSpec... typeSpecs) {
        return compileFiles(additionalProcessors, Stream.of(typeSpecs)
                .map(typeSpec -> JavaFile.builder(packageName, typeSpec).build())
                .map(JavaFile::toJavaFileObject)
                .collect(Collectors.toList()));
    }

    protected Compilation compileFiles(String packageName, TypeSpec... typeSpecs) {
        return compileFiles(Collections.emptyList(), packageName, typeSpecs);
    }

}

package eighty4.akimbo.compile.source.provides;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.service.PropertyValueParameter;

import java.util.Collection;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;

public class ProvidesComponentMethod implements ProvidesMethod {

    private final AkimboComponentDefinition componentDefinition;

    public ProvidesComponentMethod(AkimboComponentDefinition componentDefinition) {
        this.componentDefinition = componentDefinition;
    }

    @Override
    public TypeName getReturnType() {
        return componentDefinition.getType();
    }

    @Override
    public String getMethodName() {
        return "provide" + upperCaseFirstChar(componentDefinition.getName());
    }

    @Override
    public Collection<ParameterSpec> buildMethodParameters() {
        return componentDefinition.getInjectionParameters().stream()
                .filter(parameter -> !(parameter instanceof PropertyValueParameter))
                .map(parameter -> {
                    ParameterSpec.Builder dependencyParameter = ParameterSpec.builder(parameter.getType(), parameter.getName());
                    parameter.getQualifier().ifPresent(qualifier -> dependencyParameter.addAnnotation(buildNamedAnnotation(qualifier)));
                    return dependencyParameter.build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public CodeBlock buildMethodBody() {
        CodeBlock.Builder methodBody = CodeBlock.builder();
        methodBody.add(new EnvironmentValueAssignments(componentDefinition.getProperties()).toSpec());
        String parameterNames = componentDefinition.getInjectionParameters().stream()
                .map(Parameter::getName)
                .collect(Collectors.joining(", "));
        return methodBody
                .addStatement("return new $T($L)", getReturnType(), parameterNames)
                .build();
    }

}

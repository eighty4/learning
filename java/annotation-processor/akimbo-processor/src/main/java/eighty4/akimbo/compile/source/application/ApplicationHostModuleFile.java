package eighty4.akimbo.compile.source.application;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import dagger.Provides;
import eighty4.akimbo.AkimboApplicationHost;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.source.module.ModuleFile;
import eighty4.akimbo.compile.util.AkimboProcessorUtils;

import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.getNameFromTypeName;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

public class ApplicationHostModuleFile extends ModuleFile {

    private final Collection<ServiceRegistrar<?>> serviceRegistrars;

    public ApplicationHostModuleFile(String packageName, Collection<ServiceRegistrar<?>> serviceRegistrars) {
        super(packageName, "AkimboApplicationHost_Module");
        this.serviceRegistrars = serviceRegistrars.stream()
                .filter(ServiceRegistrar::hasRegisteredComponents)
                .collect(Collectors.toList());
    }

    @Override
    protected Collection<MethodSpec> getProvidesMethods() {
        return Set.of(MethodSpec.methodBuilder("provideAkimboApplicationHost")
                .addAnnotation(Provides.class)
                .addAnnotation(Singleton.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(AkimboApplicationHost.class)
                .addParameters(buildServiceHostParameters())
                .addCode(CodeBlock.builder()
                        .addStatement("return new $T($T.of($L))", AkimboApplicationHost.class, List.class, serviceRegistrars.stream()
                                .map(ServiceRegistrar::getServiceHostType)
                                .map(AkimboProcessorUtils::getNameFromTypeName)
                                .map(AkimboProcessorUtils::lowerCaseFirstChar)
                                .collect(Collectors.joining(", ")))
                        .build())
                .build());
    }

    private Collection<ParameterSpec> buildServiceHostParameters() {
        return serviceRegistrars.stream()
                .map(ServiceRegistrar::getServiceHostType)
                .map(serviceHostType -> ParameterSpec.builder(serviceHostType, lowerCaseFirstChar(getNameFromTypeName(serviceHostType))).build())
                .collect(Collectors.toSet());
    }

    @Override
    protected Set<ClassName> getModuleDependencies() {
        return serviceRegistrars.stream()
                .map(ServiceRegistrar::getServiceHostModuleType)
                .collect(Collectors.toSet());
    }
}

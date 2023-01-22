package eighty4.akimbo.compile.source.module;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ModuleResolution;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

public class ResolvedModuleFile extends ModuleFile {

    private final ModuleResolution moduleResolution;

    private final ProcessorContext processorContext;

    public ResolvedModuleFile(ModuleResolution moduleResolution, ProcessorContext processorContext) {
        super(moduleResolution.getModuleType());
        this.moduleResolution = moduleResolution;
        this.processorContext = processorContext;
    }

    @Override
    protected Set<ClassName> getModuleDependencies() {
        ComponentRegistry componentRegistry = processorContext.getComponentRegistry();
        return moduleResolution.getComponentResolutions().stream()
                .map(ComponentResolution::getDefinition)
                .map(AkimboComponentDefinition::getComponentDependencies)
                .flatMap(componentDependencies -> componentDependencies.stream().map(componentRegistry::getModuleTypeForComponent))
                .filter(moduleType -> !moduleType.equals(getType()))
                .collect(Collectors.toSet());
    }

    @Override
    protected Collection<MethodSpec> getProvidesMethods() {
        Deque<MethodSpec> providesMethods = new ArrayDeque<>();
        moduleResolution.getComponentResolutions().forEach(componentResolution -> {
            providesMethods.addAll(processorContext.getServiceRegistrars().stream()
                    .map(serviceRegistrar -> serviceRegistrar.getComponentModuleProvidesMethods(componentResolution.getDefinition()))
                    .flatMap(Collection::stream)
                    .map(ProvidesMethod::toSpec)
                    .collect(Collectors.toList()));
            providesMethods.add(componentResolution.getProvidesMethod());
        });
        return providesMethods;
    }

}

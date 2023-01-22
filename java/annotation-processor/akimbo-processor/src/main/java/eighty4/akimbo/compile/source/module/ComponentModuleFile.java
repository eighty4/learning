package eighty4.akimbo.compile.source.module;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.source.provides.ProvidesComponentMethod;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;

/**
 * @deprecated Unresolved service components from source types should be resolved by creating modules
 */
@Deprecated
public class ComponentModuleFile extends ModuleFile {

    private final AkimboComponentDefinition component;

    private final ProcessorContext processorContext;

    public ComponentModuleFile(AkimboComponentDefinition component, ProcessorContext processorContext) {
        this(component.getPackageName(), component, processorContext);
    }

    public ComponentModuleFile(String packageName, AkimboComponentDefinition component, ProcessorContext processorContext) {
        super(packageName, upperCaseFirstChar(component.getName()) + "_Module");
        this.component = component;
        this.processorContext = processorContext;
    }

    @Override
    protected Set<ClassName> getModuleDependencies() {
        ComponentRegistry componentRegistry = processorContext.getComponentRegistry();
        return component.getComponentDependencies().stream()
                .map(componentRegistry::getModuleTypeForComponent)
                .collect(Collectors.toSet());
    }

    @Override
    protected Collection<MethodSpec> getProvidesMethods() {
        List<MethodSpec> methodSpecs = processorContext.getServiceRegistrars().stream()
                .map(serviceRegistrar -> serviceRegistrar.getComponentModuleProvidesMethods(component))
                .flatMap(Collection::stream)
                .map(ProvidesMethod::toSpec)
                .collect(Collectors.toList());
        methodSpecs.add(new ProvidesComponentMethod(component).toSpec());
        return methodSpecs;
    }

}

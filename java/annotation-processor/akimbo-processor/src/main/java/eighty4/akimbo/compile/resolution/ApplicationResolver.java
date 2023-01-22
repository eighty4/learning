package eighty4.akimbo.compile.resolution;

import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.module.AkimboModule;
import eighty4.akimbo.compile.resolution.expression.ExpressionResolver;
import eighty4.akimbo.compile.source.SourceWriter;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboElementUtils.getAkimboAppElement;
import static eighty4.akimbo.compile.util.AkimboElementUtils.isConstructableComponent;
import static eighty4.akimbo.compile.util.AkimboElementUtils.isDaggerModule;

public class ApplicationResolver {

    private final ProcessorContext processorContext;

    private final ComponentResolver componentResolver;

    private final ModuleResolver moduleResolver;

    private final ExpressionResolver expressionResolver;

    private final SourceWriter sourceWriter;

    public ApplicationResolver(ComponentResolver componentResolver, ModuleResolver moduleResolver,
                               ProcessorContext processorContext, SourceWriter sourceWriter,
                               ExpressionResolver expressionResolver) {
        this.componentResolver = componentResolver;
        this.moduleResolver = moduleResolver;
        this.processorContext = processorContext;
        this.sourceWriter = sourceWriter;
        this.expressionResolver = expressionResolver;
    }

    public void addSources(Set<TypeElement> elements) {
        if (isNotInitialized()) {
            TypeElement akimboAppElement = getAkimboAppElement(elements);
            processorContext.initialize(akimboAppElement);
            moduleResolver.addModuleFromAkimboApp(akimboAppElement);
        }
        elements.forEach(typeElement -> {
            if (processorContext.getRound() == 0) addUserSource(typeElement);
            else addGeneratedSource(typeElement);
        });
        expressionResolver.getResolvableExpressions().forEach(componentResolver::addComponentResolution);
        generateAkimboSources(elements.isEmpty());
    }

    private boolean isNotInitialized() {
        return processorContext.getPackageName() == null;
    }

    private void addUserSource(TypeElement typeElement) {
        processorContext.getTypeRegistry().addUserSource(typeElement);
        if (isDaggerModule(typeElement)) {
            moduleResolver.addModuleFromUserSource(typeElement);
        } else if (isConstructableComponent(typeElement)) {
            componentResolver.addComponentFromElement(typeElement);
        }
    }

    private void addGeneratedSource(TypeElement typeElement) {
        processorContext.getTypeRegistry().addTypeElement(typeElement);
        if (isDaggerModule(typeElement)) {
            if (sourceWriter.isWrittenSourceFile(typeElement)) {
                moduleResolver.setGeneratedModuleAsResolved(typeElement);
            } else {
                moduleResolver.addModuleFromExtension(typeElement);
            }
        }
    }

    private void generateAkimboSources(boolean roundHadNoSourcesToProcess) {
        Collection<ModuleResolution> resolvableModules = moduleResolver.getResolvableModules();
        sourceWriter.writeModules(resolvableModules);
        if (processorContext.isAkimboProcessor() && moduleResolver.hasResolvedAllModules()) {
            Collection<ComponentResolution> resolvableComponents = componentResolver.getResolvableComponents();
            if (!resolvableComponents.isEmpty()) {
                moduleResolver.createComponentModules(resolvableComponents);
                sourceWriter.writeComponentModules(resolvableComponents);
            } else if (componentResolver.hasResolvedAllComponents()) {
                sourceWriter.writeAkimboWiring();
            } else if (roundHadNoSourcesToProcess) {
                String commaSeparatedUnresolvedComponents = processorContext.getComponentRegistry()
                        .getReferencesByFilter(cr -> cr.getState() == ResolutionState.UNRESOLVABLE)
                        .stream()
                        .map(AkimboComponentReference::getName)
                        .collect(Collectors.joining(", "));
                throw new IllegalStateException("Finished processing with unresolved components: " + commaSeparatedUnresolvedComponents);
            }
        }
    }

    public void addModule(AkimboModule module) {
        if (isNotInitialized()) {
            throw new IllegalStateException();
        }
        moduleResolver.addModule(module);
        expressionResolver.getResolvableExpressions().forEach(componentResolver::addComponentResolution);
    }

}

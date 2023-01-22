package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import dagger.Module;
import eighty4.akimbo.annotation.AkimboApp;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.ProvidedAkimboComponent;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.module.AkimboModule;

import javax.inject.Named;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.resolution.ModuleSource.DECLARED;
import static eighty4.akimbo.compile.resolution.ModuleSource.EXTENSION;
import static eighty4.akimbo.compile.resolution.ModuleSource.INCLUDED;
import static eighty4.akimbo.compile.resolution.ModuleSource.USER_SOURCE;
import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVABLE;
import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVED;
import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVING;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNREGISTERED;
import static eighty4.akimbo.compile.resolution.ResolutionState.UNRESOLVABLE;
import static eighty4.akimbo.compile.util.AkimboElementUtils.findProvidesMethods;
import static eighty4.akimbo.compile.util.AkimboElementUtils.getAnnotationValue;
import static eighty4.akimbo.compile.util.AkimboElementUtils.isConstructableComponent;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.getComponentNameFromProvidesMethodName;

public class ModuleResolver {

    private final ElementResolver elementResolver;

    private final ProcessorContext processorContext;

    private final ComponentResolver componentResolver;

    private final Map<ClassName, ModuleResolution> moduleResolutions = new HashMap<>();

    public ModuleResolver(ElementResolver elementResolver, ProcessorContext processorContext,
                          ComponentResolver componentResolver) {
        this.elementResolver = elementResolver;
        this.processorContext = processorContext;
        this.componentResolver = componentResolver;
    }

    public void addModuleFromUserSource(TypeElement typeElement) {
        addModuleFromElement(typeElement, UNREGISTERED, USER_SOURCE);
    }

    public void addModuleFromExtension(TypeElement typeElement) {
        addModuleFromElement(typeElement, RESOLVED, EXTENSION);
    }

    public void addModuleFromAkimboApp(TypeElement akimboAppOrModuleElement) {
        try {
            Module moduleAnnotation = akimboAppOrModuleElement.getAnnotation(Module.class);
            if (moduleAnnotation != null) {
                moduleAnnotation.includes();
            }
            AkimboApp akimboAppAnnotation = akimboAppOrModuleElement.getAnnotation(AkimboApp.class);
            if (akimboAppAnnotation != null) {
                akimboAppAnnotation.includes();
            }
        } catch (MirroredTypesException e) {
            e.getTypeMirrors().forEach(typeMirror -> addModuleFromElement(elementResolver.getTypeElement(typeMirror), RESOLVED, INCLUDED));
        }
    }

    private void addModuleFromElement(TypeElement typeElement, ResolutionState resolutionState, ModuleSource moduleSource) {
        ModuleResolution existingModuleResolution = moduleResolutions.get(ClassName.get(typeElement));
        if (existingModuleResolution != null) {
            if (resolutionState == UNREGISTERED) {
                return;
            } else {
                throw new IllegalStateException("Wtf");
            }
        }
        ClassName moduleType = ClassName.get(typeElement);
        List<ComponentResolution> providedComponents = resolutionState == RESOLVED
                ? getProvidedComponentsFromModule(typeElement, moduleType)
                : Collections.emptyList();
        ModuleResolution moduleResolution = new ModuleResolution(moduleType, moduleSource, providedComponents, resolutionState);
        moduleResolutions.put(moduleType, moduleResolution);
    }

    private List<ComponentResolution> getProvidedComponentsFromModule(TypeElement typeElement, ClassName moduleType) {
        return findProvidesMethods(typeElement).stream()
                .map(providesMethod -> getProvidedComponentDefinition(providesMethod, moduleType))
                .map(componentDefinition -> new ComponentResolution(componentDefinition, RESOLVED))
                .peek(componentResolver::addComponentResolution)
                .collect(Collectors.toList());
    }

    private AkimboComponentDefinition getProvidedComponentDefinition(ExecutableElement providesMethod, ClassName moduleType) {
        String componentName = getComponentNameFromProvidesMethodName(providesMethod.getSimpleName().toString());
        TypeName componentType = TypeName.get(providesMethod.getReturnType());
        String qualifiedName = getAnnotationValue(providesMethod, Named.class, String.class, "value").orElse(null);

        return elementResolver.getTypeElement(componentType)
                .<AkimboComponentDefinition>map(typeElement -> new ServiceAkimboComponent(typeElement, qualifiedName, moduleType))
                .orElseGet(() -> new ProvidedAkimboComponent(componentName, componentType, qualifiedName, moduleType));
    }

    public void addModule(AkimboModule module) {
        ClassName moduleType = getModuleType(module);
        if (moduleResolutions.containsKey(moduleType)) {
            throw new IllegalArgumentException(moduleType.simpleName() + " already exists -- is it being added multiple times?");
        }

        List<ComponentResolution> componentResolutions = module.getComponents().stream()
                .map(component -> {
                    if (component.getType() != null) {
                        TypeElement typeElement = getComponentTypeElement(component.getType());
                        AkimboComponentDefinition componentDefinition = new ServiceAkimboComponent(typeElement, moduleType);
                        return componentResolver.createComponentResolutionFromDefinition(componentDefinition, UNRESOLVABLE);
                    } else if (component.getResolutionExpression() != null) {
                        return componentResolver.createComponentResolutionFromExpression(component.getResolutionExpression(), moduleType);
                    } else {
                        throw new IllegalStateException("AkimboComponent must have an explicit type or an expression to be resolved");
                    }
                })
                .collect(Collectors.toList());

        ModuleResolution moduleResolution = new ModuleResolution(moduleType, DECLARED, componentResolutions);
        moduleResolutions.put(moduleType, moduleResolution);

        moduleResolution.updateResolutionState();
    }

    private ClassName getModuleType(AkimboModule module) {
        String packageName = module.getPackageName();
        if (packageName.startsWith("@@")) {
            packageName = processorContext.getAkimboSourcesPackageName() + "." + packageName.substring(2);
        }
        if (packageName.startsWith("@")) {
            packageName = packageName.substring(1);
        }
        String moduleName = module.getName();
        if (!moduleName.endsWith("_Module")) {
            moduleName += "_Module";
        }
        return ClassName.bestGuess(packageName + "." + moduleName);
    }

    private TypeElement getComponentTypeElement(ClassName className) {
        TypeElement typeElement = elementResolver.getTypeElement(className);
        if (isConstructableComponent(typeElement)) {
            return typeElement;
        } else {
            throw new IllegalStateException("An explicitly declared component is not a constructable component type: " + typeElement.getQualifiedName());
        }
    }

    Collection<ModuleResolution> getResolvableModules() {
        return moduleResolutions.values().stream()
                .filter(moduleResolution -> moduleResolution.getState() == RESOLVABLE)
                .collect(Collectors.toList());
    }

    public boolean hasResolvedAllModules() {
        return moduleResolutions.values().stream().allMatch(moduleResolution -> moduleResolution.getState() == RESOLVED);
    }

    public void setGeneratedModuleAsResolved(TypeElement typeElement) {
        ClassName moduleType = ClassName.get(typeElement);
        ModuleResolution moduleResolution = moduleResolutions.get(moduleType);
        if (moduleResolution == null) {
            return;
        }
        if (moduleResolution.getState() != RESOLVABLE && moduleResolution.getState() != RESOLVING) {
            throw new IllegalStateException("Should've been in resolvable state? " + moduleType.reflectionName());
        }

        moduleResolution.getComponentResolutions().forEach(this::resolveComponent);
        moduleResolution.setResolved();
    }

    private void resolveComponent(ComponentResolution componentResolution) {
        componentResolution.setState(RESOLVED);
        processorContext.getEventWriter().writeComponentStateChange(componentResolution);
    }

    public Collection<ModuleResolution> getModuleResolutions() {
        return moduleResolutions.values();
    }

    public void createComponentModules(Collection<ComponentResolution> componentResolutions) {
        componentResolutions.stream()
                .map(componentResolution -> new ModuleResolution(componentResolution.getDefinition().getModuleType(), USER_SOURCE, componentResolution))
                .forEach(moduleResolution -> moduleResolutions.put(moduleResolution.getModuleType(), moduleResolution));
    }

}

package eighty4.akimbo.compile;

import eighty4.akimbo.annotation.AkimboApp;
import eighty4.akimbo.compile.module.AkimboModule;
import eighty4.akimbo.compile.resolution.ApplicationResolver;
import eighty4.akimbo.compile.resolution.ComponentGraph;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.resolution.ComponentResolver;
import eighty4.akimbo.compile.resolution.ElementResolver;
import eighty4.akimbo.compile.resolution.ModuleResolver;
import eighty4.akimbo.compile.resolution.expression.ExpressionResolver;
import eighty4.akimbo.compile.service.ServiceRegistry;
import eighty4.akimbo.compile.source.SourceFile;
import eighty4.akimbo.compile.source.SourceWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboElementUtils.isGeneratedDaggerSource;

public abstract class BaseAkimboProcessor extends AbstractProcessor {

    private final ComponentGraph componentGraph = new ComponentGraph();

    private final ServiceRegistry serviceRegistry = new ServiceRegistry();

    protected ProcessorContext processorContext;

    protected ElementResolver elementResolver;

    private ModuleResolver moduleResolver;

    private ApplicationResolver applicationResolver;

    private SourceWriter sourceWriter;

    @Override
    public synchronized final void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementResolver = new ElementResolver(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
        boolean isDebugEnabled = isDebugEnabled();
        ComponentRegistry componentRegistry = new ComponentRegistry(componentGraph);
        processorContext = new ProcessorContext(this instanceof AkimboProcessor, componentRegistry, serviceRegistry, elementResolver, isDebugEnabled);
        sourceWriter = new SourceWriter(processorContext, processingEnv.getFiler());
        ExpressionResolver expressionResolver = new ExpressionResolver(processorContext);
        ComponentResolver componentResolver = new ComponentResolver(processorContext, expressionResolver, componentGraph, serviceRegistry);
        moduleResolver = new ModuleResolver(elementResolver, processorContext, componentResolver);
        applicationResolver = new ApplicationResolver(componentResolver, moduleResolver, processorContext, sourceWriter, expressionResolver);
        initializeResolution(applicationResolver);
    }

    protected void initializeResolution(ApplicationResolver applicationResolver) {

    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotationMatches, RoundEnvironment roundEnvironment) {
        try {
            processorContext.getEventWriter().writeRoundStart();
            Set<TypeElement> userSources = getUserSources(roundEnvironment);
            applicationResolver.addSources(userSources);
            boolean result = processSources(userSources);
            processorContext.getEventWriter().writeRoundState(componentGraph, moduleResolver);
            processorContext.incrementRound();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error running processor: " + Optional.ofNullable(e.getMessage()).orElse(e.getClass().toString()));
            processorContext.getEventWriter().writeErrorState(componentGraph, moduleResolver);
            return false;
        }
    }

    private Set<TypeElement> getUserSources(RoundEnvironment roundEnvironment) {
        return roundEnvironment.getRootElements().stream()
                .map(e -> (TypeElement) e)
                .filter(e -> !isGeneratedDaggerSource(e))
                .collect(Collectors.toSet());
    }

    protected abstract boolean processSources(Set<TypeElement> userSources);

    protected void writeSourceFile(SourceFile sourceFile) {
        sourceWriter.writeSourceFile(sourceFile);
    }

    protected void addModule(AkimboModule module) {
        applicationResolver.addModule(module);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(AkimboApp.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Set.of("akimbo.debug");
    }

    private boolean isDebugEnabled() {
        return Set.of("true", "yes", "1", "enabled").contains(this.processingEnv.getOptions().getOrDefault("akimbo.debug", "false"));
    }

}

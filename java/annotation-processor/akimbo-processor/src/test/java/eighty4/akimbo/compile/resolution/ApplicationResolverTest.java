package eighty4.akimbo.compile.resolution;

import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponent;
import eighty4.akimbo.compile.module.AkimboModule;
import eighty4.akimbo.compile.resolution.expression.ExpressionResolver;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.service.ServiceRegistry;
import eighty4.akimbo.compile.source.SourceWriter;
import eighty4.akimbo.compile.util.CompilationTest;
import eighty4.akimbo.compile.util.TestTypes;
import eighty4.akimbo.compile.util.TestTypes.DataApi;
import eighty4.akimbo.compile.util.TestTypes.HttpServiceWithDependency;
import eighty4.akimbo.compile.util.TestTypes.HttpServiceWithoutDependencies;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ApplicationResolverTest extends CompilationTest {

    private ComponentGraph componentGraph;

    private ApplicationResolver applicationResolver;

    private ModuleResolver moduleResolver;

    private ServiceRegistry serviceRegistry;

    @Before
    public void setUp() {
        componentGraph = new ComponentGraph();
        serviceRegistry = new ServiceRegistry();
        ComponentRegistry componentRegistry = new ComponentRegistry(componentGraph);

        ProcessorContext processorContext = new ProcessorContext(true, componentRegistry, serviceRegistry, null, true);
        ElementResolver elementResolver = new ElementResolver(rule.getTypes(), rule.getElements());
        ExpressionResolver expressionResolver = new ExpressionResolver(processorContext);
        ComponentResolver componentResolver = new ComponentResolver(processorContext, expressionResolver, componentGraph, serviceRegistry);
        moduleResolver = new ModuleResolver(elementResolver, processorContext, componentResolver);
        applicationResolver = new ApplicationResolver(componentResolver, moduleResolver, processorContext, mock(SourceWriter.class), expressionResolver);
    }

    @Test
    public void addModuleFromResolvableService_resolvesComponent() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class)));
        applicationResolver.addModule(AkimboModule.of(HttpServiceWithoutDependencies.class));

        assertThat(moduleResolver.getResolvableModules()).hasSize(1);
        assertThat(componentGraph.getComponents(RESOLVABLE)).hasSize(1);
    }

    @Test
    public void addModuleProvidingResolvableService_resolvesComponent() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class)));
        applicationResolver.addModule(AkimboModule.at("test.app.resolver", "Resolvable_Module")
                .providingComponent(AkimboComponent.of(HttpServiceWithoutDependencies.class)));

        assertThat(moduleResolver.getResolvableModules()).hasSize(1);
        assertThat(componentGraph.getComponents(RESOLVABLE)).hasSize(1);
    }

    @Test
    public void addModuleProvidingResolvableServiceAndDependency_resolvesBothComponents() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class)));
        applicationResolver.addModule(AkimboModule.at("test.app.resolver", "Resolvable_Module")
                .providingComponent(AkimboComponent.of(DataApi.class))
                .providingComponent(AkimboComponent.of(HttpServiceWithDependency.class)));

        assertThat(moduleResolver.getResolvableModules()).hasSize(1);
        assertThat(componentGraph.getComponents(RESOLVABLE)).hasSize(2);
    }

    @Test
    public void includeAppModuleProvidingService_registersComponentsWithServiceRegistrars() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestAppIncludingModule.class)));

        assertThat(serviceRegistry.getServiceRegistrars().stream().anyMatch(ServiceRegistrar::hasServiceRegistrations)).isTrue();
    }

    @Test
    public void addModuleWithExpressionResolution_withinSameModule() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class)));
        applicationResolver.addModule(AkimboModule.at("test.app.resolver", "Resolvable_Module")
                .providingComponent(AkimboComponent.of(HttpServiceWithDependency.class))
                .providingComponent(AkimboComponent.of("#httpServiceWithDependency.dataApi")));

        assertThat(moduleResolver.getResolvableModules()).hasSize(1);
        assertThat(componentGraph.getComponents(RESOLVABLE)).hasSize(2);
        List<?> serviceRegistrations = serviceRegistry.getServiceRegistrars().stream()
                .map(ServiceRegistrar::getServiceRegistrations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(serviceRegistrations).hasSize(1);
    }

    @Test
    public void addModulesWithExpressionResolution_withAnotherModule() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class)));
        applicationResolver.addModule(AkimboModule.at("test.app.resolver", "HttpService_Module")
                .providingComponent(AkimboComponent.of(HttpServiceWithDependency.class)));
        applicationResolver.addModule(AkimboModule.at("test.app.resolver", "DataApi_Module")
                .providingComponent(AkimboComponent.of("#httpServiceWithDependency.dataApi")));

        moduleResolver.getModuleResolutions().forEach(moduleResolution -> {
            System.out.println(moduleResolution.getModuleType().simpleName() + " - " + moduleResolution.getState());
            moduleResolution.getComponentResolutions().forEach(componentResolution ->
                    System.out.println("  " + componentResolution.getReference().getName() + " - " + componentResolution.getState()));
        });

        assertThat(moduleResolver.getResolvableModules()).hasSize(2);
        assertThat(componentGraph.getComponents(RESOLVABLE)).hasSize(2);
        List<?> serviceRegistrations = serviceRegistry.getServiceRegistrars().stream()
                .map(ServiceRegistrar::getServiceRegistrations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertThat(serviceRegistrations).hasSize(1);
    }

    static class SomeType {}

    @Test
    public void test() {
        applicationResolver.addSources(Set.of(getTypeElement(TestTypes.TestApp.class), getTypeElement(SomeType.class)));

        assertThat(componentGraph.getComponents(cr -> !cr.getState().isRegistered()));
    }

}

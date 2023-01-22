package eighty4.akimbo.compile.resolution;

import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.resolution.expression.ExpressionResolver;
import eighty4.akimbo.compile.util.CompilationTest;
import eighty4.akimbo.compile.service.ServiceRegistry;
import eighty4.akimbo.compile.http.HttpServiceRegistrar;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ComponentResolverTest extends CompilationTest {

    private ComponentGraph componentGraph;

    private ComponentResolver resolver;

    @Before
    public void setUp() {
        componentGraph = new ComponentGraph();
        ComponentRegistry componentRegistry = new ComponentRegistry(componentGraph);
        ServiceRegistry serviceRegistry = new ServiceRegistry(List.of(new HttpServiceRegistrar("")));
        ProcessorContext processorContext = new ProcessorContext(true, componentRegistry, serviceRegistry, null, false);
        ExpressionResolver expressionResolver = new ExpressionResolver(processorContext);
        resolver = new ComponentResolver(processorContext, expressionResolver, componentGraph, serviceRegistry);
    }

    @HttpService
    static class TestHttpServiceWithoutDeps {

    }

    @Test
    public void resolvableWithNoDependencies() {
        resolver.addComponentFromElement(getTypeElement(TestHttpServiceWithoutDeps.class));
        assertThat(componentGraph.getComponents(ResolutionState.RESOLVABLE)).hasSize(1);
    }

    @HttpService
    static class TestHttpServiceWithDeps {

        @Inject
        public TestHttpServiceWithDeps(TestHttpServiceWithoutDeps dep) {
        }
    }

    @Test
    public void notResolvableWithUnresolvedDependencies() {
        resolver.addComponentFromElement(getTypeElement(TestHttpServiceWithDeps.class));
        assertThat(componentGraph.getComponents(ResolutionState.RESOLVABLE)).isEmpty();
        resolver.addComponentFromElement(getTypeElement(TestHttpServiceWithoutDeps.class));
        assertThat(componentGraph.getComponents(ResolutionState.RESOLVABLE)).hasSize(2);
    }
}

package eighty4.akimbo.compile;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.debug.ConsoleWriter;
import eighty4.akimbo.compile.debug.EventWriter;
import eighty4.akimbo.compile.debug.NoopEventWriter;
import eighty4.akimbo.compile.http.HttpServiceRegistrar;
import eighty4.akimbo.compile.lifecycle.LifecycleServiceRegistrar;
import eighty4.akimbo.compile.resolution.ComponentRegistry;
import eighty4.akimbo.compile.resolution.ElementResolver;
import eighty4.akimbo.compile.resolution.TypeRegistry;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.service.ServiceRegistry;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collection;

public class ProcessorContext {

    private static final String AKIMBO_SOURCES_PACKAGE_SUFFIX = ".a_k_i_m_b_o";

    private final boolean akimboProcessor;

    private final EventWriter eventWriter;

    private final ServiceRegistry serviceRegistry;

    private final ComponentRegistry componentRegistry;

    private final TypeRegistry typeRegistry;

    private String packageName;

    private String akimboSourcesPackageName;

    private int round = 0;

    public ProcessorContext(boolean akimboProcessor, ComponentRegistry componentRegistry,
                            ServiceRegistry serviceRegistry, ElementResolver elementResolver,
                            boolean isDebugEnabled) {
        this.akimboProcessor = akimboProcessor;
        this.componentRegistry = componentRegistry;
        this.serviceRegistry = serviceRegistry;
        eventWriter = isDebugEnabled ? new ConsoleWriter(this) : new NoopEventWriter();
        typeRegistry = new TypeRegistry(elementResolver, eventWriter);
    }

    public void initialize(Element akimboAppElement) {
        packageName = ClassName.get((TypeElement) akimboAppElement).packageName();
        akimboSourcesPackageName = packageName + AKIMBO_SOURCES_PACKAGE_SUFFIX;
        serviceRegistry.addServiceRegistrar(new HttpServiceRegistrar(akimboSourcesPackageName));
        serviceRegistry.addServiceRegistrar(new LifecycleServiceRegistrar(akimboSourcesPackageName));
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAkimboSourcesPackageName() {
        return akimboSourcesPackageName;
    }

    public Collection<ServiceRegistrar<?>> getServiceRegistrars() {
        return serviceRegistry.getServiceRegistrars();
    }

    public boolean isAkimboProcessor() {
        return akimboProcessor;
    }

    public boolean isExtensionProcessor() {
        return !akimboProcessor;
    }

    public void incrementRound() {
        round++;
    }

    public int getRound() {
        return round;
    }

    public TypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    public EventWriter getEventWriter() {
        return eventWriter;
    }

    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }

}

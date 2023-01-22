package eighty4.akimbo.compile.service;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.source.SourceFile;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ServiceRegistrar<T extends ServiceRegistration<?>> {

    private final String akimboSourcesPackageName;

    private final Map<AkimboComponentDefinition, T> serviceRegistrations = new HashMap<>();

    public ServiceRegistrar(String akimboSourcesPackageName) {
        this.akimboSourcesPackageName = akimboSourcesPackageName;
    }

    public Optional<T> getServiceRegistration(AkimboComponentDefinition component) {
        return Optional.ofNullable(serviceRegistrations.get(component));
    }

    public boolean hasServiceRegistrations() {
        return !serviceRegistrations.isEmpty();
    }

    public Collection<T> getServiceRegistrations() {
        return serviceRegistrations.values();
    }

    public abstract String getServiceHostSubPackage();

    protected abstract boolean isComponentToRegister(ServiceAkimboComponent component);

    protected abstract T createServiceRegistration(ServiceAkimboComponent component);

    public boolean addComponent(ServiceAkimboComponent componentDefinition) {
        if (isComponentToRegister(componentDefinition)) {
            T serviceRegistration = createServiceRegistration(componentDefinition);
            serviceRegistration.buildMethodRegistrations();
            serviceRegistrations.put(componentDefinition, serviceRegistration);
            return true;
        }
        return false;
    }

    public boolean hasRegisteredComponents() {
        return !serviceRegistrations.isEmpty();
    }

    public abstract ClassName getServiceHostType();

    public List<ProvidesMethod> getComponentModuleProvidesMethods(AkimboComponentDefinition component) {
        return Collections.emptyList();
    }

    public ClassName getServiceHostModuleType() {
        return ClassName.bestGuess(akimboSourcesPackageName + "." + getServiceHostSubPackage() + "." + getServiceHostType().simpleName() + "_Module");
    }

    public abstract List<ProvidesMethod> getHostModuleProvidesMethods();

    public SourceFile buildHostSourceFile() {
        return new ServiceHostModuleFile<>(this);
    }

    public List<SourceFile> buildComponentSourceFiles(AkimboComponentDefinition component) {
        return List.of();
    }
}

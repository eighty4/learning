package eighty4.akimbo.compile.service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import eighty4.akimbo.compile.source.module.ModuleFile;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceHostModuleFile<T extends ServiceRegistration<?>> extends ModuleFile {

    private final ServiceRegistrar<T> serviceRegistrar;

    public ServiceHostModuleFile(ServiceRegistrar<T> serviceRegistrar) {
        super(serviceRegistrar.getServiceHostModuleType());
        this.serviceRegistrar = serviceRegistrar;
    }

    @Override
    protected Collection<MethodSpec> getProvidesMethods() {
        return serviceRegistrar.getHostModuleProvidesMethods().stream()
                .map(ProvidesMethod::toSpec)
                .collect(Collectors.toList());
    }

    @Override
    protected Set<ClassName> getModuleDependencies() {
        return serviceRegistrar.getServiceRegistrations().stream()
                .map(serviceRegistration -> serviceRegistration.getComponent().getModuleType())
                .collect(Collectors.toSet());
    }

}

package eighty4.akimbo.compile.service;

import eighty4.akimbo.compile.component.ServiceAkimboComponent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class ServiceRegistry {

    private final Deque<ServiceRegistrar<?>> serviceRegistrars;

    public ServiceRegistry() {
        serviceRegistrars = new ArrayDeque<>(5);
    }

    public ServiceRegistry(Collection<ServiceRegistrar<?>> serviceRegistrars) {
        this.serviceRegistrars = new ArrayDeque<>(serviceRegistrars);
    }

    public void addServiceRegistrar(ServiceRegistrar<?> serviceRegistrar) {
        serviceRegistrars.add(serviceRegistrar);
    }

    public boolean addComponent(ServiceAkimboComponent componentDefinition) {
        boolean serviceRegistered = false;
        for (ServiceRegistrar<?> serviceRegistrar : serviceRegistrars) {
            if (serviceRegistrar.addComponent(componentDefinition)) {
                serviceRegistered = true;
            }
        }
        return serviceRegistered;
    }

    public Collection<ServiceRegistrar<?>> getServiceRegistrars() {
        return new ArrayDeque<>(serviceRegistrars);
    }
}

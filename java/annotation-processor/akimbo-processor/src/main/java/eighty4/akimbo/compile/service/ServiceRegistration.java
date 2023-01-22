package eighty4.akimbo.compile.service;

import eighty4.akimbo.compile.component.ServiceAkimboComponent;

import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboElementUtils.findMethods;

public abstract class ServiceRegistration<T extends ServiceMethodRegistration> {
    private final ServiceAkimboComponent component;
    private final List<T> serviceMethodRegistrations = new ArrayList<>();

    public ServiceRegistration(ServiceAkimboComponent component) {
        this.component = component;
    }

    public String getComponentName() {
        return getComponent().getName();
    }

    public ServiceAkimboComponent getComponent() {
        return component;
    }

    public List<T> getServiceMethodRegistrations() {
        return serviceMethodRegistrations;
    }

    void buildMethodRegistrations() {
        serviceMethodRegistrations.addAll(findMethods(getComponent().getTypeElement()).stream()
                .map(this::buildMethodRegistration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    protected abstract Optional<T> buildMethodRegistration(ExecutableElement method);

}

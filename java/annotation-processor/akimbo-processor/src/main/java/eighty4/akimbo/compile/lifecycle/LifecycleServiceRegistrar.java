package eighty4.akimbo.compile.lifecycle;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;
import eighty4.akimbo.lifecycle.AkimboLifecycleServiceHost;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Set;

import static eighty4.akimbo.compile.util.AkimboElementUtils.hasMethodsWithAnnotations;

public class LifecycleServiceRegistrar extends ServiceRegistrar<LifecycleServiceRegistration> {

    public LifecycleServiceRegistrar(String akimboSourcesPackageName) {
        super(akimboSourcesPackageName);
    }

    @Override
    public ClassName getServiceHostType() {
        return ClassName.get(AkimboLifecycleServiceHost.class);
    }

    @Override
    public String getServiceHostSubPackage() {
        return "lifecycle";
    }

    @Override
    protected boolean isComponentToRegister(ServiceAkimboComponent typeRegistration) {
        return hasMethodsWithAnnotations(typeRegistration.getTypeElement(), Set.of(PostConstruct.class, PreDestroy.class));
    }

    @Override
    protected LifecycleServiceRegistration createServiceRegistration(ServiceAkimboComponent component) {
        return new LifecycleServiceRegistration(component);
    }

    @Override
    public List<ProvidesMethod> getHostModuleProvidesMethods() {
        return List.of(
                new ProvidesLifecycleHooksMethod(getServiceRegistrations()),
                new ProvidesLifecycleHostMethod()
        );
    }
}

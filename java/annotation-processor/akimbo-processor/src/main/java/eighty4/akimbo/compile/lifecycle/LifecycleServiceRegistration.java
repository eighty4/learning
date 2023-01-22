package eighty4.akimbo.compile.lifecycle;

import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.service.ServiceRegistration;
import eighty4.akimbo.lifecycle.LifecyclePhase;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.lang.model.element.ExecutableElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static eighty4.akimbo.compile.util.AkimboElementUtils.hasAnnotation;

public class LifecycleServiceRegistration extends ServiceRegistration<LifecycleHookRegistration> {

    public LifecycleServiceRegistration(ServiceAkimboComponent component) {
        super(component);
    }

    @Override
    protected Optional<LifecycleHookRegistration> buildMethodRegistration(ExecutableElement method) {
        Set<LifecyclePhase> lifecyclePhases = new HashSet<>();
        if (hasAnnotation(method, PostConstruct.class)) {
            lifecyclePhases.add(LifecyclePhase.STARTUP);
        }
        if (hasAnnotation(method, PreDestroy.class)) {
            lifecyclePhases.add(LifecyclePhase.SHUTDOWN);
        }
        if (lifecyclePhases.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new LifecycleHookRegistration(method, lifecyclePhases));
        }
    }
}

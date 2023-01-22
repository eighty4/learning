package eighty4.akimbo.compile.lifecycle;

import eighty4.akimbo.compile.service.ServiceMethodRegistration;
import eighty4.akimbo.lifecycle.LifecyclePhase;

import javax.lang.model.element.ExecutableElement;
import java.util.Set;

public class LifecycleHookRegistration extends ServiceMethodRegistration {

    private final Set<LifecyclePhase> lifecyclePhases;

    public LifecycleHookRegistration(ExecutableElement executableElement, Set<LifecyclePhase> lifecyclePhases) {
        super(executableElement);
        this.lifecyclePhases = lifecyclePhases;
    }

    public Set<LifecyclePhase> getLifecyclePhases() {
        return lifecyclePhases;
    }
}

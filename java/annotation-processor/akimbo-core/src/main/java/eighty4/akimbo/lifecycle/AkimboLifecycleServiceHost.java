package eighty4.akimbo.lifecycle;

import com.google.common.flogger.FluentLogger;
import eighty4.akimbo.AkimboServiceHost;

import java.util.List;
import java.util.Map;

public class AkimboLifecycleServiceHost implements AkimboServiceHost {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final Map<LifecyclePhase, List<LifecycleHook>> lifecycleHooks;

    public AkimboLifecycleServiceHost(Map<LifecyclePhase, List<LifecycleHook>> lifecycleHooks) {
        this.lifecycleHooks = lifecycleHooks;
    }

    @Override
    public void startServices() {
        execute(LifecyclePhase.STARTUP);
    }

    @Override
    public void stopServices() {
        execute(LifecyclePhase.SHUTDOWN);
    }

    private void execute(LifecyclePhase phase) {
        List<LifecycleHook> hooks = lifecycleHooks.get(phase);
        if (hooks == null || hooks.isEmpty()) {
            return;
        }
        logger.atInfo().log("Running %s hooks", phase);
        for (LifecycleHook lifecycleHook : lifecycleHooks.get(phase)) {
            try {
                logger.atInfo().log("Executing %s hook %s", phase, lifecycleHook);
                lifecycleHook.getRunnable().run();
            } catch (Exception e) {
                logger.atSevere().withCause(e).log("Error executing life cycle hook %s", lifecycleHook);
                e.printStackTrace();
            }
        }
    }
}

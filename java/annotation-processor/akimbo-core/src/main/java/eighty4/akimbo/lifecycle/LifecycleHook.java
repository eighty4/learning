package eighty4.akimbo.lifecycle;

public class LifecycleHook {
    private String componentName;
    private String methodName;
    private Runnable runnable;

    public LifecycleHook(String componentName, String methodName, Runnable runnable) {
        this.componentName = componentName;
        this.methodName = methodName;
        this.runnable = runnable;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public String toString() {
        return componentName + "." + methodName;
    }
}

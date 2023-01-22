package eighty4.akimbo.lifecycle;

public enum LifecyclePhase {
    STARTUP, SHUTDOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

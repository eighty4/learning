package eighty4.akimbo.compile.resolution;

public enum ResolutionState {
    UNREGISTERED, UNRESOLVABLE, RESOLVABLE, RESOLVING, RESOLVED;

    public boolean isResolvable() {
        return this == RESOLVABLE || this == RESOLVING || this == RESOLVED;
    }

    public boolean isRegistered() {
        return this != UNREGISTERED;
    }

}

package eighty4.akimbo.compile.module;

import eighty4.akimbo.compile.component.AkimboComponent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;

public class AkimboModule {

    public static AkimboModule at(String packageName, String name) {
        return new AkimboModule(packageName, name);
    }

    public static AkimboModule of(Class<?> type) {
        return AkimboModule.of(AkimboComponent.of(type));
    }

    public static AkimboModule of(AkimboComponent component) {
        String packageName = component.getType().packageName();
        String name = component.getType().simpleName() + "_Module";
        return new AkimboModule(packageName, name).providingComponent(component);
    }

    private final String name;

    private final String packageName;

    private final Deque<AkimboComponent> components = new ArrayDeque<>();

    private AkimboModule(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Collection<AkimboComponent> getComponents() {
        return new ArrayDeque<>(components);
    }

    public AkimboModule providingComponent(AkimboComponent component) {
        components.add(component);
        return this;
    }

    @Override
    public String toString() {
        return packageName + "." + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AkimboModule that = (AkimboModule) o;
        return name.equals(that.name) && packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, packageName);
    }
}

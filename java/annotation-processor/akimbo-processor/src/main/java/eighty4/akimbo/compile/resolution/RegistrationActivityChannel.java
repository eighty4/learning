package eighty4.akimbo.compile.resolution;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.component.AkimboComponentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivityChannel {

    private static final RegistrationActivityChannel INSTANCE = new RegistrationActivityChannel();

    public static RegistrationActivityChannel getInstance() {
        return INSTANCE;
    }

    public interface RegistrationEventHandler<T> {
        void handleEvent(T eventData);
    }

    private final Map<String, List<RegistrationEventHandler<AkimboComponentReference>>> componentHandlers = new HashMap<>();

    private final Map<ClassName, List<RegistrationEventHandler<ClassName>>> typeHandlers = new HashMap<>();

    private RegistrationActivityChannel() {

    }

    public void onComponentRegistered(String componentName, RegistrationEventHandler<AkimboComponentReference> handler) {
        componentHandlers.compute(componentName, (s, handlers) -> {
            if (handlers == null) {
                handlers = new ArrayList<>();
            }
            handlers.add(handler);
            return handlers;
        });
    }

    public void onTypeRegistered(ClassName typeName, RegistrationEventHandler<ClassName> handler) {
        typeHandlers.compute(typeName, (s, handlers) -> {
            if (handlers == null) {
                handlers = new ArrayList<>();
            }
            handlers.add(handler);
            return handlers;
        });
    }

    public void announceTypeRegistered(ClassName className) {
        typeHandlers.computeIfPresent(className, (cn, handlers) -> {
            handlers.forEach(handler -> handler.handleEvent(className));
            handlers.clear();
            return handlers;
        });
    }

    public void announceComponentRegistered(AkimboComponentReference componentReference) {
        componentHandlers.computeIfPresent(componentReference.getName(), (cn, handlers) -> {
            handlers.forEach(handler -> handler.handleEvent(componentReference));
            handlers.clear();
            return handlers;
        });
    }

}

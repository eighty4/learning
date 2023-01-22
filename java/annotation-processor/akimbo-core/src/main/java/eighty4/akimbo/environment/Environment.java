package eighty4.akimbo.environment;

public class Environment {

    public String getProperty(String key) {
        return EnvironmentAccess.getProperty(key);
    }

    public <T> T getProperty(String key, Class<T> returnType) {
        return EnvironmentAccess.getProperty(key, returnType);
    }
}

package eighty4.akimbo.environment;

import eighty4.akimbo.ValueUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// todo yaml, profiles, fallback values for @Value parameters
public class EnvironmentAccess {

    private static final Properties DEFAULTS;

    private static final Map<String, String> DEFAULT_KEYS_TO_ENV_VARS;

    static {
        DEFAULTS = new Properties();
        InputStream inputStream = EnvironmentAccess.class.getResourceAsStream("/defaults.properties");
        try {
            DEFAULTS.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read default properties file", e);
        }

        Map<String, String> keyMap = new HashMap<>();
        DEFAULTS.forEach((propKey, value) -> {
            String envVarKey = propKeyToEnvVar(propKey);
            keyMap.put(envVarKey, propKey.toString());
        });
        DEFAULT_KEYS_TO_ENV_VARS = new HashMap<>(keyMap);
    }

    private static String propKeyToEnvVar(Object propKey) {
        return propKey.toString()
                .toUpperCase()
                .replaceAll("-", "_")
                .replaceAll("\\.", "_");
    }

    public static String getProperty(String key) {
        return getProperty(key, (String) null);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = System.getenv(propKeyToEnvVar(key));
        if (value != null) {
            return value;
        } else if (DEFAULTS.containsKey(key)) {
            return DEFAULTS.getProperty(key);
        } else {
            String propertyKey = DEFAULT_KEYS_TO_ENV_VARS.get(key);
            if (propertyKey != null) {
                return DEFAULTS.getProperty(propertyKey);
            }
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        throw new IllegalArgumentException("No env var or property found for " + key);
    }

    public static <T> T getProperty(String key, Class<T> returnType) {
        return getProperty(key, null, returnType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(String key, String defaultValue, Class<T> returnType) {
        String property = getProperty(key, defaultValue);
        if (property.getClass().isAssignableFrom(returnType)) {
            return (T) property;
        } else {
            return ValueUtil.asType(property, returnType);
        }
    }
}

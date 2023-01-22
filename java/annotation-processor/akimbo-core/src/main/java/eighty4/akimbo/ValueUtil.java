package eighty4.akimbo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ValueUtil {

    private static final List<String> trueBooleans = List.of("1", "true", "yes");

    @SuppressWarnings("unchecked")
    public static <T> T asType(String value, Class<T> type) {
        String trimmed = value.trim();
        if (Boolean.class == type || boolean.class == type) {
            return (T) asBoolean(trimmed);
        } else if (Integer.class == type || int.class == type) {
            return (T) asInteger(trimmed);
        } else if (Long.class == type || long.class == type) {
            return (T) asLong(trimmed);
        } else if (BigInteger.class == type) {
            return (T) asBigInteger(trimmed);
        } else if (BigDecimal.class == type) {
            return (T) asBigDecimal(trimmed);
        } else if (UUID.class == type) {
            return (T) asUUID(trimmed);
        } else if (Double.class == type || double.class == type) {
            return (T) asDouble(trimmed);
        } else if (Float.class == type || float.class == type) {
            return (T) asFloat(trimmed);
        } else if (Short.class == type || short.class == type) {
            return (T) asShort(trimmed);
        }
        throw new IllegalArgumentException("unsupported type?");
    }

    public static Boolean asBoolean(String value) {
        String lowerCaseValue = value.toLowerCase();
        return trueBooleans.stream().anyMatch(trueBoolean -> trueBoolean.equals(lowerCaseValue));
    }

    public static BigInteger asBigInteger(String value) {
        return new BigInteger(value);
    }

    public static BigDecimal asBigDecimal(String value) {
        return new BigDecimal(value);
    }

    public static UUID asUUID(String value) {
        return UUID.fromString(value);
    }

    public static Long asLong(String value) {
        return Long.valueOf(value);
    }

    public static Short asShort(String value) {
        return Short.valueOf(value);
    }

    public static Double asDouble(String value) {
        return Double.valueOf(value);
    }

    public static Integer asInteger(String value) {
        return Integer.valueOf(value);
    }

    public static Float asFloat(String value) {
        return Float.valueOf(value);
    }

    public static String defaultValue(String defaultValue, String value) {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public static <T> T required(T value) {
        if (value == null) {
            throw new IllegalStateException("missing required value -- make me a 400");
        }
        return value;
    }
}

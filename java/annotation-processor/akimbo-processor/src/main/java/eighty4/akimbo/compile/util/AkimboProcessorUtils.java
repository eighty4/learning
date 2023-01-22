package eighty4.akimbo.compile.util;

import com.squareup.javapoet.TypeName;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AkimboProcessorUtils {

    public static String upperCaseFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String lowerCaseFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String getNameFromFqn(String fqn) {
        if (fqn.contains(".")) {
            return fqn.substring(fqn.lastIndexOf(".") + 1);
        } else {
            return fqn;
        }
    }

    public static String getNameFromTypeName(TypeName type) {
        return getNameFromFqn(type.toString());
    }

    public static String getComponentNameFromProvidesMethodName(String providesMethodName) {
        if (providesMethodName.startsWith("provides")) {
            providesMethodName = providesMethodName.substring(8);
        }
        if (providesMethodName.startsWith("provide")) {
            providesMethodName = providesMethodName.substring(7);
        }
        return lowerCaseFirstChar(providesMethodName);
    }

    public static String propertyKeyToVariableName(String propertyKey) {
        String[] split = propertyKey.toLowerCase()
                .replaceAll("\\.", "-")
                .replaceAll("_", "-")
                .split("-");
        return lowerCaseFirstChar(Stream.of(split).map(AkimboProcessorUtils::upperCaseFirstChar).collect(Collectors.joining()));
    }
}

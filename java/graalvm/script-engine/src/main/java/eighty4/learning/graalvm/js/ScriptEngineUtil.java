package eighty4.learning.graalvm.js;

import org.graalvm.polyglot.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScriptEngineUtil {

    public static BufferedReader readScript(String scriptName) {
        var scriptStream = ScriptEngineUtil.class.getResourceAsStream("/%s".formatted(scriptName));
        if (scriptStream == null) {
            throw new IllegalStateException("%s is not a classpath resource".formatted(scriptName));
        }
        return new BufferedReader(new InputStreamReader(scriptStream));
    }

    public static Source sourceFromScript(String scriptName) throws IOException {
        return Source.newBuilder("js", readScript(scriptName), scriptName)
                .mimeType("application/javascript+module").build();
    }

    public static Source sourceFromString(String source) throws IOException {
        return Source.newBuilder("js", source, "script.js")
                .mimeType("application/javascript+module").build();
    }
}

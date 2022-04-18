package eighty4.learning.graalvm.js;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class FunInvokingJavaMethod {

    public static class FunActivities {

        private final List<String> activities = new ArrayList<>();

        public void addActivity(String activity) {
            activities.add(activity);
        }

        public List<String> getActivities() {
            return List.copyOf(activities);
        }
    }

    public static void main(String[] args) throws ScriptException {

        try (var engine = GraalJSScriptEngine.create(null,
                Context.newBuilder("js")
                        .allowHostAccess(HostAccess.ALL)
                        .allowHostClassLookup(s -> true)
                        .option("js.ecmascript-version", "2022"))) {

            var activities = new FunActivities();
            engine.put("activities", activities);
            engine.eval("activities.addActivity('bake cookies')");

            assert(activities.getActivities().get(0).equals("bake cookies"));

            System.out.println(activities.getActivities());
        }
    }
}

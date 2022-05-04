package eighty4.learning.graalvm.js;

import java.util.ArrayList;
import java.util.List;

public class FunActivities {

    private final List<String> activities = new ArrayList<>();

    public void addActivity(String activity) {
        activities.add(activity);
    }

    public List<String> getActivities() {
        return List.copyOf(activities);
    }
}

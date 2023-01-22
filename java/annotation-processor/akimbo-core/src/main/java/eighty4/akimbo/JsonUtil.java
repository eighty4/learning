package eighty4.akimbo;

import com.google.gson.Gson;

// todo configurable api
public class JsonUtil {

    private static final Gson GSON = new Gson();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

}

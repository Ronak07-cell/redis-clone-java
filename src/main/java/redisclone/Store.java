package redisclone;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private final Map<String, String> data = new HashMap<>();

    public synchronized void set(String key, String value) {
        data.put(key, value);
    }

    public synchronized String get(String key) {
        return data.get(key);
    }

    public synchronized boolean delete(String key) {
        return data.remove(key) != null;
    }

    public synchronized boolean exists(String key) {
        return data.containsKey(key);
    }
}

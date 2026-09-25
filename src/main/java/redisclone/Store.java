package redisclone;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private final Map<String, String> data = new HashMap<>();
    private final Map<String, Long> expiryTimes = new HashMap<>();

    public synchronized void set(String key, String value) {
        data.put(key, value);
        expiryTimes.remove(key);
    }

    public synchronized String get(String key) {
        if (isExpired(key)) {
            return null;
        }
        return data.get(key);
    }

    public synchronized boolean delete(String key) {
        expiryTimes.remove(key);
        return data.remove(key) != null;
    }

    public synchronized boolean exists(String key) {
        if (isExpired(key)) {
            return false;
        }
        return data.containsKey(key);
    }

    public synchronized boolean expire(String key, long seconds) {
        if (!data.containsKey(key)) {
            return false;
        }
        long expiryTime = System.currentTimeMillis() + (seconds * 1000);
        expiryTimes.put(key, expiryTime);
        return true;
    }

    private boolean isExpired(String key) {
        Long expiryTime = expiryTimes.get(key);
        if (expiryTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiryTime) {
            data.remove(key);
            expiryTimes.remove(key);
            return true;
        }
        return false;
    }
}

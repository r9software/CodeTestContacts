package com.middevs.local.db.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author MidDevs
 * @since 7/22/17
 */

public class MidevsCache {

    private final int TOTAL_CACHE = 1000;

    private HashMap<String, Object> collectionCache;

    MidevsCache() {

        collectionCache = new HashMap<>();
    }

    public void add(String collectionName,
                    String key,
                    Object value) {

        collectionCache.put(collectionName + key, value);
        limitCache();
    }

    public Object get(String collectionName,
                      String key) {

        return collectionCache.get(collectionName + key);
    }

    public List<Object> getAll(String collectionName) {

        List<Object> list = new ArrayList<>();
        for (String key : collectionCache.keySet()) {
            if (key.startsWith(collectionName)) {
                Object v = collectionCache.get(key);
                if (v != null) list.add(v);
            }
        }
        return list;
    }

    public void remove(String collectionName,
                       String key) {

        collectionCache.remove(collectionName + key);
    }

    public void remove(String collectionName) {

        for (String key : collectionCache.keySet()) {
            if (key.startsWith(collectionName)) {
                collectionCache.remove(key);
            }
        }
    }

    private void limitCache() {

        if (collectionCache.size() > TOTAL_CACHE) {
            Iterator iterator = collectionCache.values()
                    .iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
                break;
            }
        }
    }
}

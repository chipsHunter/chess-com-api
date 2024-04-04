package hvorostina.chesscomapi.in_memory_cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RequestCache<T> {
    private final HashMap<String, T> cache;
    private final static int MAX_SIZE = 20;
    public RequestCache() {
        this.cache = new LinkedHashMap<>(MAX_SIZE) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                return size() >= MAX_SIZE;
            }
        };
    }
    public void removeQuery(String query) {
        cache.remove(query);
    }
    public void clear() {
        cache.clear();
    }
    public boolean containsQuery(String query) {
        return cache.containsKey(query);
    }
    public void putQuery(String query, T response) {
        cache.put(query, response);
    }
    public void updateQuery(String query, T response) {
        cache.replace(query, response);
    }
    public T getResponse(String query) {
        return cache.get(query);
    }
    public void clear() {
        cache.clear();
    }
}
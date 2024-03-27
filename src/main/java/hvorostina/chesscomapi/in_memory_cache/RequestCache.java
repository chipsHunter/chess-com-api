package hvorostina.chesscomapi.in_memory_cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RequestCache {
    private final HashMap<String, Object> cache;
    private final int MAX_SIZE = 20;
    public RequestCache() {
        this.cache = new LinkedHashMap<>(MAX_SIZE) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() >= MAX_SIZE;
            }
        };
    }
    public void removeQuery(String query) {
        cache.remove(query);
    }
    public boolean containsQuery(String query) {
        return cache.containsKey(query);
    }
    public Object addQuery(String query, Object response) {
        cache.put(query, response);
        return response;
    }
    public Object updateResponse(String query, Object response) {
        cache.replace(query, response);
        return response;
    }
    public Object getResponse(String query) {
        if(!containsQuery(query))
            return null;
        return cache.get(query);
    }
}
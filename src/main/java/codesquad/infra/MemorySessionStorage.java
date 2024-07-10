package codesquad.infra;

import codesquad.application.handler.SessionStorage;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionStorage implements SessionStorage {
    private final ConcurrentHashMap<String, Object> sessionStorage = new ConcurrentHashMap<>();

    @Override
    public String store(Object o) {
        UUID uuid = UUID.randomUUID();
        while (sessionStorage.containsKey(uuid.toString())) {
            uuid = UUID.randomUUID();
        }
        sessionStorage.put(uuid.toString(), o);
        return uuid.toString();
    }

    @Override
    public Object get(String sid) {
        return sessionStorage.get(sid);
    }

    @Override
    public void invalidate(String sid) {
        sessionStorage.remove(sid);
    }
}

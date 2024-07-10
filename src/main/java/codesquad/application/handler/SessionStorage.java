package codesquad.application.handler;

public interface SessionStorage {
    String store(Object o);

    Object get(String sid);

    void invalidate(String sid);
}

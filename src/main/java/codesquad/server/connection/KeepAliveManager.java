package codesquad.server.connection;

public interface KeepAliveManager {

    boolean isAlive();

    boolean isTimeout();

    void incrementRequest();
}

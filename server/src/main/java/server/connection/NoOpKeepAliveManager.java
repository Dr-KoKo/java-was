package server.connection;

public class NoOpKeepAliveManager implements KeepAliveManager {
    private boolean firstRequest;

    public NoOpKeepAliveManager() {
        this.firstRequest = true;
    }

    @Override
    public boolean isAlive() {
        return firstRequest;
    }

    @Override
    public boolean isTimeout() {
        return false;
    }

    @Override
    public void incrementRequest() {
        firstRequest = false;
    }
}

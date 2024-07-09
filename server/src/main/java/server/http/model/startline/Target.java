package server.http.model.startline;

public class Target {
    private final String path;

    public Target(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Target{" +
                "path='" + path + '\'' +
                '}';
    }
}

package server.http.model.startline;

public class RequestLine {
    private final Version version;
    private final Method method;
    private final Target target;

    public RequestLine(Version version, Method method, Target target) {
        this.version = version;
        this.method = method;
        this.target = target;
    }

    public Version getVersion() {
        return version;
    }

    public Method getMethod() {
        return method;
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "version=" + version +
                ", method=" + method +
                ", target=" + target +
                '}';
    }
}

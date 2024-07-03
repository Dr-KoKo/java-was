package codesquad.http.model.startline;

public class StatusLine {
    private final Version version;
    private final StatusCode statusCode;

    public StatusLine(Version version, StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return version.getMessage() + " " + statusCode.getCode() + " " + statusCode.getMessage() + "\n";
    }
}

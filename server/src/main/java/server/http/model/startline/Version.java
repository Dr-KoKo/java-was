package server.http.model.startline;

public enum Version {
    HTTP_1_1("HTTP/1.1");

    private final String message;

    Version(String message) {
        this.message = message;
    }

    public static Version of(String message) {
        for (Version version : Version.values()) {
            if (version.getMessage().equals(message)) {
                return version;
            }
        }
        throw new UnsupportedOperationException("Unsupported HTTP version: " + message);
    }

    public String getMessage() {
        return message;
    }
}

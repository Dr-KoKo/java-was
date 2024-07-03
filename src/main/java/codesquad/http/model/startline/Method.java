package codesquad.http.model.startline;

public enum Method {
    GET, POST, PUT, DELETE;

    public static Method of(String message) {
        for (Method method : Method.values()) {
            if (method.toString().equals(message)) {
                return method;
            }
        }
        throw new UnsupportedOperationException("Unsupported method: " + message);
    }
}

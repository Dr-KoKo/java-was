package server.exception;

import server.http.model.startline.StatusCode;

public class ResponseException extends RuntimeException {
    private final StatusCode statusCode;

    public ResponseException(String message, StatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}

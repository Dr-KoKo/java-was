package server.exception;

public class ConnectedSocketException extends ServerException {
    public ConnectedSocketException(String message) {
        super(message);
    }

    public ConnectedSocketException(Throwable cause) {
        super(cause);
    }
}

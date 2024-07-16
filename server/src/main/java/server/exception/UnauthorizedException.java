package server.exception;

public class UnauthorizedException extends ConnectedSocketException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}

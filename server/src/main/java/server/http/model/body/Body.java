package server.http.model.body;

public class Body {
    private final byte[] message;

    public Body(byte[] message) {
        this.message = message;
    }

    public byte[] getMessage() {
        return message;
    }
}

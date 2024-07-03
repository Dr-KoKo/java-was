package codesquad.http.model.body;

public class Body {
    private final byte[] body;

    public Body(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}

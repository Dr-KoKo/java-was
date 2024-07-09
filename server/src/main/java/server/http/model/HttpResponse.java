package server.http.model;

import server.http.model.body.Body;
import server.http.model.header.Headers;
import server.http.model.startline.StatusLine;

public class HttpResponse {
    private final StatusLine statusLine;
    private final Headers headers;
    private final Body body;

    public HttpResponse(StatusLine statusLine) {
        this(statusLine, new Headers(), null);
    }

    public HttpResponse(StatusLine statusLine, Headers headers, Body body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeader() {
        return headers;
    }

    public Body getBody() {
        return body;
    }

    public boolean hasBody() {
        return body != null;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}

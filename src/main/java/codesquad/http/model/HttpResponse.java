package codesquad.http.model;

import codesquad.http.model.body.Body;
import codesquad.http.model.header.Headers;
import codesquad.http.model.startline.StatusCode;
import codesquad.http.model.startline.StatusLine;
import codesquad.http.model.startline.Version;

public class HttpResponse {
    public static final HttpResponse BAD_REQUEST = new BadRequestHttpResponse();

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

    private static class BadRequestHttpResponse extends HttpResponse {
        public BadRequestHttpResponse() {
            super(new StatusLine(Version.HTTP_1_1, StatusCode.BAD_REQUEST), null, null);
        }
    }
}

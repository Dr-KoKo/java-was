package server.http.model;

import server.http.model.body.Body;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

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

    public static HttpResponse ok(byte[] content, String contentType) {
        Headers headers = new Headers();
        headers.addHeader("Content-Type", contentType);
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.OK), headers, new Body(content));
    }

    public static HttpResponse found(String location) {
        Headers headers = new Headers();
        headers.addHeader("Location", location);
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.FOUND), headers, null);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.NOT_FOUND));
    }

    public static HttpResponse unauthorized() {
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.UNAUTHORIZED));
    }

    public static HttpResponse internalServerError() {
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.INTERNAL_SERVER_ERROR));
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

package server.http.model;

import server.http.model.body.Body;
import server.http.model.header.ContentType;
import server.http.model.header.Header;
import server.http.model.header.Headers;
import server.http.model.startline.Method;
import server.http.model.startline.RequestLine;
import server.http.model.startline.Target;
import server.http.model.startline.Version;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(RequestLine requestLine) {
        this(requestLine, new Headers());
    }

    public HttpRequest(RequestLine requestLine, Headers headers) {
        this(requestLine, headers, null);
    }

    public HttpRequest(RequestLine requestLine, Headers headers, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean hasBody() {
        return headers.get(Header.CONTENT_LENGTH.getFieldName()) != null;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(Header.CONTENT_LENGTH.getFieldName()));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeader() {
        return headers;
    }

    public Body getBody() {
        return body;
    }

    public String getRequestPath() {
        return requestLine.getTarget().getPath().split("\\?")[0];
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public Map<String, String> getQueryString() {
        if (!requestLine.getTarget().getPath().contains("?")) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        String queryString = requestLine.getTarget().getPath().split("\\?")[1];
        for (String query : queryString.split("&")) {
            result.put(query.split("=")[0], query.split("=")[1]);
        }
        return result;
    }

    public ContentType getContentType() {
        return ContentType.of(headers.get(Header.CONTENT_TYPE.getFieldName()));
    }

    public HttpRequest forward(Method method, Target target) {
        return new HttpRequest(new RequestLine(Version.HTTP_1_1, method, target), headers, body);
    }
}

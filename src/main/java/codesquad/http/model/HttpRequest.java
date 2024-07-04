package codesquad.http.model;

import codesquad.http.model.body.Body;
import codesquad.http.model.header.Header;
import codesquad.http.model.header.Headers;
import codesquad.http.model.startline.Method;
import codesquad.http.model.startline.RequestLine;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers headers;
    private final Body message;

    public HttpRequest(RequestLine requestLine) {
        this(requestLine, null);
    }

    public HttpRequest(RequestLine requestLine, Headers headers) {
        this(requestLine, headers, null);
    }

    public HttpRequest(RequestLine requestLine, Headers headers, Body message) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.message = message;
    }

    public boolean hasBody() {
        return headers.get(Header.CONTENT_LENGTH.getFieldName()) != null;
    }

    public int getContentLength() {
        return (int) headers.get(Header.CONTENT_LENGTH.getFieldName());
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeader() {
        return headers;
    }

    public Body getMessage() {
        return message;
    }

    public String getRequestPath() {
        return requestLine.getTarget().getPath().split("\\?")[0];
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public Map<String, String> getQueryString() {
        Map<String, String> result = new HashMap<>();
        String queryString = requestLine.getTarget().getPath().split("\\?")[1];
        for (String query : queryString.split("&")) {
            result.put(query.split("=")[0], query.split("=")[1]);
        }
        return result;
    }
}

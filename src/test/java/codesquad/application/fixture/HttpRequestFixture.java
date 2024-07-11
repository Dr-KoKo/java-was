package codesquad.application.fixture;

import server.http.model.HttpRequest;
import server.http.model.body.Body;
import server.http.model.header.Headers;
import server.http.model.startline.Method;
import server.http.model.startline.RequestLine;
import server.http.model.startline.Target;
import server.http.model.startline.Version;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface HttpRequestFixture {
    default RequestLine requestLine(Method method, String requestPath) {
        return new RequestLine(Version.HTTP_1_1, method, new Target(requestPath));
    }

    default HttpRequest simpleRequest(Method method, String requestPath) {
        return new HttpRequest(requestLine(method, requestPath));
    }

    default Headers formDataHeader(int contentLength) {
        Headers headers = new Headers();
        headers.addHeader("content-type", "application/x-www-form-urlencoded");
        headers.addHeader("content-length", String.valueOf(contentLength));
        return headers;
    }

    default Body formDataBody(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.setLength(sb.length() - 1);
        return new Body(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    default HttpRequest formDataRequest(Method method, String requestPath, Map<String, String> params) {
        Body body = formDataBody(params);
        Headers headers = formDataHeader(body.getMessage().length);
        return new HttpRequest(requestLine(method, requestPath), headers, body);
    }
}

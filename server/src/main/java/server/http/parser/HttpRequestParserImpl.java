package server.http.parser;

import server.exception.BadGrammarException;
import server.http.model.HttpRequest;
import server.http.model.body.Body;
import server.http.model.header.Headers;
import server.http.model.startline.Method;
import server.http.model.startline.RequestLine;
import server.http.model.startline.Target;
import server.http.model.startline.Version;

import java.nio.charset.StandardCharsets;

public class HttpRequestParserImpl implements HttpRequestParser {
    @Override
    public HttpRequest parseRequestLine(String requestLine) throws BadGrammarException {
        Method method = Method.of(requestLine.split(" ")[0]);
        Target target = new Target(requestLine.split(" ")[1]);
        Version version = Version.of(requestLine.split(" ")[2]);
        return new HttpRequest(new RequestLine(version, method, target));
    }

    @Override
    public HttpRequest parseHeader(HttpRequest httpRequest, String headers) throws BadGrammarException {
        Headers result = new Headers();
        for (String header : headers.split("\n")) {
            String fieldName = header.split(":")[0];
            String fieldValue = header.split(":")[1].trim();
            result.addHeader(fieldName, fieldValue);
        }
        return new HttpRequest(httpRequest.getRequestLine(), result);
    }

    @Override
    public HttpRequest parseBody(HttpRequest httpRequest, String body) throws BadGrammarException {
        return new HttpRequest(httpRequest.getRequestLine(), httpRequest.getHeader(), new Body(body.getBytes(StandardCharsets.UTF_8)));
    }
}

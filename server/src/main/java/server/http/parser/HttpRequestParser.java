package server.http.parser;

import server.exception.BadGrammarException;
import server.http.model.HttpRequest;

public interface HttpRequestParser {
    HttpRequest parseRequestLine(String requestLine) throws BadGrammarException;

    HttpRequest parseHeader(HttpRequest httpRequest, String headers) throws BadGrammarException;

    HttpRequest parseBody(HttpRequest httpRequest, byte[] body) throws BadGrammarException;
}

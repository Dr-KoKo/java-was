package codesquad.http.parser;

import codesquad.exception.BadGrammarException;
import codesquad.http.model.HttpRequest;

public interface HttpRequestParser {
    HttpRequest parseRequestLine(String requestLine) throws BadGrammarException;

    HttpRequest parseHeader(HttpRequest httpRequest, String headers) throws BadGrammarException;

    HttpRequest parseBody(HttpRequest httpRequest, String body) throws BadGrammarException;
}

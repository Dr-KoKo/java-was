package codesquad.application.parser;

import server.http.model.header.ContentType;

import java.util.Map;

public interface BodyParser {
    Map<String, String> parse(String body);

    boolean supports(ContentType contentType);
}

package codesquad.application.handler;

import codesquad.application.parser.BodyParser;
import server.http.model.header.ContentType;

import java.util.Map;

public class StubBodyParser implements BodyParser {
    @Override
    public Map<String, String> parse(byte[] body) {
        return Map.of("userId", "donghar", "password", "password", "nickname", "dr-koko");
    }

    @Override
    public boolean supports(ContentType contentType) {
        return true;
    }
}

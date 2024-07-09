package codesquad.application.parser;

import server.http.model.header.ContentType;

import java.util.HashMap;
import java.util.Map;

public class FormDataBodyParser implements BodyParser {
    @Override
    public Map<String, String> parse(String body) {
        Map<String, String> result = new HashMap<>();
        for (String query : body.split("&")) {
            result.put(query.split("=")[0], query.split("=")[1]);
        }
        return result;
    }

    @Override
    public boolean supports(ContentType contentType) {
        return ContentType.FORM_DATA == contentType;
    }
}

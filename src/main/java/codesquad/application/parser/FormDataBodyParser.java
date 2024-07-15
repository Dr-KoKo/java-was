package codesquad.application.parser;

import server.http.model.header.ContentType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormDataBodyParser implements BodyParser {
    @Override
    public Map<String, String> parse(byte[] body) {
        Map<String, String> result = new HashMap<>();
        String bodyString = new String(body, StandardCharsets.UTF_8);
        for (String query : bodyString.split("&")) {
            String[] keyValue = query.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public boolean supports(ContentType contentType) {
        return ContentType.FORM_DATA == contentType;
    }
}

package codesquad.application.argumentresolver;

import server.http.model.HttpRequest;
import server.http.model.header.ContentType;

import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormUrlEncodedResolver implements ArgumentResolver<Map<String, String>> {
    @Override
    public Map<String, String> resolve(Parameter parameter, HttpRequest request) {
        Map<String, String> result = new HashMap<>();
        String bodyString = new String(request.getBody().getMessage(), StandardCharsets.UTF_8);
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
    public boolean support(Parameter parameter, HttpRequest request) {
        return request.getContentType() == ContentType.FORM_DATA && parameter.getType().isAssignableFrom(Map.class);
    }
}

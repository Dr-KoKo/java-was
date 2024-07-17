package codesquad.application.argumentresolver;

import codesquad.annotation.api.parameter.QueryString;
import server.http.model.HttpRequest;

import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryStringArgumentResolver implements ArgumentResolver<Map<String, String>> {
    @Override
    public Map<String, String> resolve(Parameter parameter, HttpRequest request) {
        Map<String, String> result = new HashMap<>();
        String bodyString = request.getRequestPath().split("\\?")[1];
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
        return parameter.isAnnotationPresent(QueryString.class) && parameter.getType().isAssignableFrom(Map.class);
    }
}

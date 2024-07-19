package server.http.model.header;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    private final Map<String, String> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String get(String fieldName) {
        return headers.get(fieldName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}

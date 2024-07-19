package codesquad.application.argumentresolver;

import codesquad.annotation.api.parameter.Multipart;
import server.http.model.HttpRequest;
import server.http.model.header.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultipartArgumentResolver implements ArgumentResolver<Map<String, byte[]>> {
    @Override
    public Map<String, byte[]> resolve(Parameter parameter, HttpRequest request) {
        // extract boundary
        String boundary = getBoundary(request);
        if (boundary == null || boundary.isBlank()) {
            throw new UnsupportedOperationException("No boundary found");
        }
        boundary = "--" + boundary;

        // parse body
        byte[] body = request.getBody().getMessage();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        Map<String, byte[]> resultMap = new HashMap<>();
        try {
            String line;
            while ((line = readLine(inputStream)) != null) {
                if (line.equals(boundary)) {
                    // Process each part
                    String headers = readUntilCrlf(inputStream);
                    String contentDisposition = getContentDisposition(headers);
                    byte[] content = readContent(inputStream, boundary);
                    resultMap.put(contentDisposition, content);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resultMap;
    }

    private String getContentDisposition(String headers) {
        String[] lines = headers.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("Content-Disposition")) {
                String[] parts = line.split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("name")) {
                        return part.split("=")[1].trim().replace("\"", "");
                    }
                }
            }
        }
        return null;
    }

    private byte[] readContent(InputStream inputStream, String boundary) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int prev = -1, current;
        while ((current = inputStream.read()) != -1) {
            if (prev == '\r' && current == '\n') {
                if (checkBoundary(inputStream, boundary)) {
                    break;
                }
            }
            if (prev != -1) {
                buffer.write(prev);
            }
            prev = current;
        }
        if (prev != -1) {
            buffer.write(prev);
        }
        return buffer.toByteArray();
    }

    private boolean checkBoundary(InputStream inputStream, String boundary) throws IOException {
        inputStream.mark(boundary.length() + 2);
        byte[] boundaryBytes = new byte[boundary.length() + 2];
        int readBytes = inputStream.read(boundaryBytes);
        String readBoundary = new String(boundaryBytes, 0, readBytes, StandardCharsets.UTF_8);
        inputStream.reset();
        return readBoundary.startsWith(boundary);
    }

    private String readUntilCrlf(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\n') {
                break;
            }
            if (c != '\r') {
                sb.append((char) c);
            }
        }
        if (sb.isEmpty() && c == -1) {
            return null;
        }
        return sb.toString();
    }

    private String getBoundary(HttpRequest request) {
        String[] contentType = request.getHeader().get(Header.CONTENT_TYPE.getFieldName()).split(";");
        for (String attribute : contentType) {
            String[] split = attribute.split("=");
            if (split.length == 2 && split[0].trim().equalsIgnoreCase("boundary")) {
                return split[1].trim();
            }
        }
        return null;
    }

    @Override
    public boolean support(Parameter parameter, HttpRequest request) {
        return parameter.isAnnotationPresent(Multipart.class) && parameter.getType().isAssignableFrom(Map.class);
    }
}

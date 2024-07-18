package server.http.model.header;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    SVG("svg", "image/svg+xml"),
    FORM_DATA("form-data", "application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multi-part", "multipart/form-data"),
    DEFAULT("", "text/plain");

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType ofExtension(String extension) {
        for (ContentType contentType : ContentType.values()) {
            if (contentType.extension.equals(extension)) {
                return contentType;
            }
        }
        return ContentType.DEFAULT;
    }

    public static ContentType of(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (ContentType contentType : ContentType.values()) {
            if (value.contains(contentType.contentType)) {
                return contentType;
            }
        }
        return null;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}

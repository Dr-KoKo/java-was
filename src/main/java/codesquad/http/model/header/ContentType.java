package codesquad.http.model.header;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    SVG("svg", "image/svg+xml"),
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

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}

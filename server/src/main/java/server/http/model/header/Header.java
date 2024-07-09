package server.http.model.header;

public enum Header {
    CONNECTION("connection"), CONTENT_TYPE("Content-Type"), CONTENT_LENGTH("Content-Length");

    private final String fieldName;

    Header(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

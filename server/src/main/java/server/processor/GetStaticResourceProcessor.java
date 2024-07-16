package server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.body.Body;
import server.http.model.header.ContentType;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GetStaticResourceProcessor implements HttpRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GetStaticResourceProcessor.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        String requestPath = request.getRequestPath();
        URL resourceUrl = getResourceUrl(requestPath);
        if (resourceUrl == null) {
            return notFoundResponse();
        }
        try (InputStream resourceStream = resourceUrl.openStream()) {
            byte[] content = resourceStream.readAllBytes();
            String contentType = getContentType(resourceUrl.getFile());
            return createResponse(content, contentType);
        } catch (IOException e) {
            logger.error("Failed to read resource: {}", requestPath, e);
            return serverErrorResponse();
        }
    }

    @Override
    public boolean matches(HttpRequest request) {
        String requestPath = request.getRequestPath();
        URL resourceUrl = getResourceUrl(requestPath);
        return resourceUrl != null;
    }

    private URL getResourceUrl(String requestPath) {
        URL resourceUrl = getClass().getResource(requestPath);
        if (resourceUrl == null) {
            logger.info("Resource not found in classpath: {}", requestPath);
        } else {
            logger.info("Resource found: {}", resourceUrl);
        }
        return resourceUrl;
    }

    private String getContentType(String filePath) {
        logger.debug("searching extension for file: {}", filePath);
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        logger.debug("extension: {}", extension);
        return ContentType.ofExtension(extension).getContentType();
    }

    private HttpResponse createResponse(byte[] content, String contentType) {
        Headers headers = new Headers();
        headers.addHeader("Content-Type", contentType);
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.OK), headers, new Body(content));
    }

    private HttpResponse notFoundResponse() {
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.NOT_FOUND));
    }

    private HttpResponse serverErrorResponse() {
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.INTERNAL_SERVER_ERROR));
    }
}

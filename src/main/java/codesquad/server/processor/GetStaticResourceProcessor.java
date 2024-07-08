package codesquad.server.processor;

import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;
import codesquad.http.model.body.Body;
import codesquad.http.model.header.ContentType;
import codesquad.http.model.header.Headers;
import codesquad.http.model.startline.Method;
import codesquad.http.model.startline.StatusCode;
import codesquad.http.model.startline.StatusLine;
import codesquad.http.model.startline.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GetStaticResourceProcessor implements HttpRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GetStaticResourceProcessor.class);

    private static final String STATIC_ROOT = "/static";

    @Override
    public HttpResponse process(HttpRequest request) {
        String requestPath = resolvePath(request);
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
    public boolean supports(HttpRequest request) {
        return request.getMethod() == Method.GET;
    }

    private String resolvePath(HttpRequest request) {
        String path = request.getRequestPath();
        if (path.isEmpty() || path.equals("/")) {
            return "/index.html";
        }
        return path;
    }

    private URL getResourceUrl(String requestPath) {
        String resourcePath = STATIC_ROOT + requestPath;
        if (!resourcePath.contains(".")) {
            resourcePath = resourcePath + "/index.html";
        }
        URL resourceUrl = getClass().getResource(resourcePath);
        if (resourceUrl == null) {
            logger.info("Resource not found in classpath: {}", resourcePath);
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

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GetStaticResourceProcessor extends StaticResourceProcessor {
    private static final String rootPath = "src/main/resources/static";

    @Override
    public HttpResponse process(HttpRequest request) {
        String requestPath = resolvePath(request);
        File file = getFile(requestPath);
        if (file == null) {
            return null;
        }
        byte[] bytes = getBytes(file);
        Headers headers = new Headers();
        addContentType(headers, getExtension(file));
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.OK), headers, new Body(bytes));
    }

    private static File getFile(String requestPath) {
        File file = requestPath.isBlank() ? new File(rootPath) : new File(rootPath, requestPath);
        if (!file.exists()) {
            return null;
        }
        if (file.isDirectory()) {
            file = new File(file, "index.html");
        }
        return file;
    }

    private String resolvePath(HttpRequest request) {
        String result = request.getRequestPath();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static void addContentType(Headers headers, String extension) {
        ContentType contentType = ContentType.ofExtension(extension);
        if (contentType != null) {
            headers.addHeader("content-type", contentType.getContentType());
        }
    }

    private static byte[] getBytes(File file) {
        byte[] bytes;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                bytes = fileInputStream.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    public static String getExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return ""; // 확장자가 없는 경우
        }
    }

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestLine().getMethod() == Method.GET;
    }
}

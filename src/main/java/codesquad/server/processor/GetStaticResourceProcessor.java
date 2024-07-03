package codesquad.server.processor;

import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;
import codesquad.http.model.body.Body;
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
        File file = new File(rootPath, request.getRequestPath());
        if (!file.exists()) {
            return null;
        }
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

        Headers headers = new Headers();
        String extension = getExtension(file);
        if ("html".equals(extension)) {
            headers.addHeader("content-type", "text/html");
        }
        return new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.OK), headers, new Body(bytes));
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

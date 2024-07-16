package codesquad.application.returnvaluehandler;

import codesquad.application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpResponse;
import server.http.model.header.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelViewHandler implements ReturnValueHandler {
    private static final Logger logger = LoggerFactory.getLogger(ModelViewHandler.class);

    @Override
    public HttpResponse handle(Object returnValue) {
        ModelView mv = (ModelView) returnValue;
        URL resourceUrl = getResourceUrl(mv.getFilePath());
        if (mv.getFilePath().equals("/templates/user/list/index.html")) {
            return resourceResponse(resourceUrl, mv.getModel().get("user") != null, (List<User>) mv.getModel().get("users"));
        }
        return resourceResponse(resourceUrl, mv.getModel().get("user") != null);
    }

    @Override
    public boolean support(Object returnValue) {
        return ModelView.class.isAssignableFrom(returnValue.getClass());
    }

    private HttpResponse resourceResponse(URL resourceUrl, boolean isLogin) {
        if (resourceUrl == null) {
            return HttpResponse.notFound();
        }
        byte[] content;
        try {
            content = getContent(resourceUrl);
            content = replaceHeader(content, isLogin);
        } catch (IOException e) {
            return HttpResponse.internalServerError();
        }
        String contentType = getContentType(resourceUrl.getFile());
        return HttpResponse.ok(content, contentType);
    }

    private HttpResponse resourceResponse(URL resourceUrl, boolean isLogin, List<User> all) {
        if (resourceUrl == null) {
            return HttpResponse.notFound();
        }
        byte[] content;
        try {
            content = getContent(resourceUrl);
            content = replaceHeader(content, isLogin);
            content = replaceUser(content, all);
        } catch (IOException e) {
            return HttpResponse.internalServerError();
        }
        String contentType = getContentType(resourceUrl.getFile());
        return HttpResponse.ok(content, contentType);
    }

    private byte[] replaceUser(byte[] content, List<User> all) {
        String target = new String(content);
        String regex = "<tbody\\b[^>]*>(.*?)</tbody>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        StringBuilder replacement = new StringBuilder();
        replacement.append("<tbody>");
        for (User user : all) {
            replacement.append("<tr>")
                    .append("<td>").append(user.getUserId()).append("</td>")
                    .append("<td>").append(user.getNickname()).append("</td>")
                    .append("</tr>");
        }
        replacement.append("</tbody>");

        Matcher matcher = pattern.matcher(target);
        String result = matcher.replaceAll(replacement.toString());
        return result.getBytes();
    }

    private byte[] replaceHeader(byte[] content, boolean isLogin) throws UnsupportedEncodingException {
        String target = new String(content);
        String regex = "<header\\b[^>]*>(.*?)</header>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        String replacement;
        try {
            URL replcaementUrl = isLogin ?
                    getResourceUrl("/templates/block/header/login_header.html") : getResourceUrl("/templates/block/header/not_login_header.html");
            byte[] replacementContent = getContent(replcaementUrl);
            replacement = new String(replacementContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Matcher matcher = pattern.matcher(target);
        String result = matcher.replaceAll(replacement);
        return result.getBytes();
    }

    private static byte[] getContent(URL resourceUrl) throws IOException {
        try (InputStream resourceStream = resourceUrl.openStream()) {
            return resourceStream.readAllBytes();
        } catch (IOException e) {
            logger.error("Failed to read resource: {}", "/index.html", e);
            throw e;
        }
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
}

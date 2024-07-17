package codesquad.application.returnvaluehandler;

import codesquad.application.model.Article;
import codesquad.application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpResponse;
import server.http.model.header.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelViewHandler implements ReturnValueHandler {
    private static final Logger logger = LoggerFactory.getLogger(ModelViewHandler.class);

    @Override
    public HttpResponse handle(Object returnValue) {
        ModelView mv = (ModelView) returnValue;
        URL resourceUrl = getResourceUrl(mv.getFilePath());
        return resourceResponse(resourceUrl, mv.getModel());
    }

    @Override
    public boolean support(Object returnValue) {
        return ModelView.class.isAssignableFrom(returnValue.getClass());
    }

    private HttpResponse resourceResponse(URL resourceUrl, Map<String, Object> model) {
        if (resourceUrl == null) {
            return HttpResponse.notFound();
        }
        byte[] content;
        try {
            content = getContent(resourceUrl);
            content = replaceHeader(content, model.containsKey("user"));
            if (model.containsKey("users")) {
                content = replaceUser(content, (List<User>) model.get("users"));
            }
            if (model.containsKey("articles")) {
                content = replaceArticle(content, (List<Article>) model.get("articles"));
            }
            if (model.containsKey("code")) {
                content = replaceError(content, model.get("code"), model.get("message"));
            }
        } catch (IOException e) {
            return HttpResponse.internalServerError();
        }
        String contentType = getContentType(resourceUrl.getFile());
        return HttpResponse.ok(content, contentType);
    }

    private byte[] replaceError(byte[] content, Object code, Object message) {
        String target = new String(content);
        String replace = target.replace("{{code}}", code.toString());
        replace = replace.replace("{{message}}", message.toString());
        return replace.getBytes();
    }

    private byte[] replaceHeader(byte[] content, boolean isLogin) {
        String target = new String(content);
        String marker = "{{header}}";
        String block;
        try {
            URL replcaementUrl = isLogin ?
                    getResourceUrl("/templates/block/header/login_header.html") : getResourceUrl("/templates/block/header/not_login_header.html");
            byte[] replacementContent = getContent(replcaementUrl);
            block = new String(replacementContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String result = target.replace(marker, block);
        return result.getBytes();
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

    private byte[] replaceArticle(byte[] content, List<Article> articles) {
        String target = new String(content);
        String marker = "{{article}}";
        String block;
        try {
            URL replcaementUrl = getResourceUrl("/templates/block/article/article.html");
            byte[] replacementContent = getContent(replcaementUrl);
            block = new String(replacementContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder replacement = new StringBuilder();
        for (Article article : articles) {
            String temp;
            temp = block.replace("{{title}}", article.getTitle());
            temp = block.replace("{{account}}", article.getAuthorId());
            temp = temp.replace("{{content}}", article.getContent());
            replacement.append(temp);
        }
        String result = target.replace(marker, replacement);
        return result.getBytes();
    }

    private byte[] getContent(URL resourceUrl) throws IOException {
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

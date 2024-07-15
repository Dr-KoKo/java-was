package codesquad.application.handler;

import codesquad.annotation.GetMapping;
import codesquad.annotation.PostMapping;
import codesquad.application.model.User;
import codesquad.application.parser.BodyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.body.Body;
import server.http.model.header.ContentType;
import server.http.model.header.Header;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final BodyParser parser;
    private final UserDao userDao;
    private final SessionStorage sessionStorage;

    public RequestHandler(BodyParser parser, UserDao userDao, SessionStorage sessionStorage) {
        this.parser = parser;
        this.userDao = userDao;
        this.sessionStorage = sessionStorage;
    }

    @GetMapping(path = "/")
    public HttpResponse index(HttpRequest request) {
        URL resourceUrl = getResourceUrl("/templates/index.html");
        return resourceResponse(resourceUrl, request);
    }

    @GetMapping(path = "/user/registration")
    public HttpResponse createUser(HttpRequest request) {
        URL resourceUrl = getResourceUrl("/templates/user/registration/index.html");
        return resourceResponse(resourceUrl, request);
    }

    @GetMapping(path = "/user/login")
    public HttpResponse loginForm(HttpRequest request) {
        String status = request.getQueryString().get("status");
        URL resourceUrl = Objects.equals(status, "fail") ?
                getResourceUrl("/templates/user/login/login_failed.html") : getResourceUrl("/templates/user/login/index.html");
        return resourceResponse(resourceUrl, request);
    }

    @GetMapping(path = "/user/list")
    public HttpResponse userList(HttpRequest request) {
        if (!isLogin(request)) {
            return HttpResponse.unauthorized();
        }
        URL resourceUrl = getResourceUrl("/templates/user/list/index.html");
        List<User> all = userDao.findAll();
        return resourceResponse(resourceUrl, request, all);
    }

    @PostMapping(path = "/user/login")
    public HttpResponse login(HttpRequest request) {
        Map<String, String> parameters = getBody(request);
        String userId = parameters.get("userId");
        String password = parameters.get("password");
        logger.debug("Login request for user {} with password {}", userId, password);

        Optional<User> findUser = userDao.findByUserId(userId);
        boolean matches = findUser.isPresent() && findUser.get().matchesPassword(password);
        if (matches) {
            StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
            Headers headers = new Headers();
            String sid = sessionStorage.store(findUser.get());
            headers.addHeader("Location", "/");
            headers.addHeader("Set-Cookie", "sid=" + sid + "; Path=/");
            return new HttpResponse(statusLine, headers, null);
        } else {
            return HttpResponse.found("/user/login?status=fail");
        }
    }

    @PostMapping(path = "/user/create")
    public HttpResponse create(HttpRequest request) {
        Map<String, String> parameters = getBody(request);
        // store user
        User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("nickname"));
        logger.debug("Creating request processor for user {}", user);

        userDao.save(user);

        return HttpResponse.found("/");
    }

    @PostMapping(path = "/logout")
    public HttpResponse logout(HttpRequest request) {
        String sessionId = getSessionId(request);
        if (sessionId != null) {
            sessionStorage.invalidate(sessionId);
        }

        StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
        Headers headers = new Headers();
        headers.addHeader("Set-Cookie", "sid=" + " " + "; Path=/" + "; Max-age=0");
        headers.addHeader("Location", "/");
        return new HttpResponse(statusLine, headers, null);
    }

    private boolean isLogin(HttpRequest request) {
        String cookie = request.getHeader().get(Header.COOKIE.getFieldName());
        return cookie != null && cookie.contains("sid");
    }

    private HttpResponse resourceResponse(URL resourceUrl, HttpRequest httpRequest) {
        if (resourceUrl == null) {
            return HttpResponse.notFound();
        }
        byte[] content;
        try {
            content = getContent(resourceUrl);
            content = replaceHeader(content, httpRequest);
        } catch (IOException e) {
            return HttpResponse.internalServerError();
        }
        String contentType = getContentType(resourceUrl.getFile());
        return HttpResponse.ok(content, contentType);
    }

    private HttpResponse resourceResponse(URL resourceUrl, HttpRequest httpRequest, List<User> all) {
        if (resourceUrl == null) {
            return HttpResponse.notFound();
        }
        byte[] content;
        try {
            content = getContent(resourceUrl);
            content = replaceHeader(content, httpRequest);
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

    private byte[] replaceHeader(byte[] content, HttpRequest httpRequest) throws UnsupportedEncodingException {
        String target = new String(content);
        String regex = "<header\\b[^>]*>(.*?)</header>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        String replacement;
        try {
            URL replcaementUrl = isLogin(httpRequest) ?
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

    private String getSessionId(HttpRequest request) {
        String cookieValue = request.getHeader().get(Header.COOKIE.getFieldName());
        for (String cookie : cookieValue.split(";")) {
            if ("sid".equals(cookie.split("=")[0])) {
                return cookie.split("=")[1];
            }
        }
        return null;
    }

    private Map<String, String> getBody(HttpRequest request) {
        Map<String, String> parameters = parser.parse(new String(request.getBody().getMessage()));
        for (Map.Entry<String, String> stringStringEntry : parameters.entrySet()) {
            logger.debug("Parameter name: {} value: {}", stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        return parameters;
    }
}

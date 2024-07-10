package codesquad.application.handler;

import codesquad.annotation.PostMapping;
import codesquad.application.model.User;
import codesquad.application.parser.BodyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.header.Header;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

import java.util.Map;
import java.util.Optional;

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

    @PostMapping(path = "/create")
    public HttpResponse create(HttpRequest request) {
        Map<String, String> parameters = getBody(request);
        // store user
        User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("nickname"));
        logger.debug("Creating request processor for user {}", user);

        userDao.save(user);

        StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
        Headers headers = new Headers();
        headers.addHeader("Location", "/index.html");
        return new HttpResponse(statusLine, headers, null);
    }

    @PostMapping(path = "/login")
    public HttpResponse login(HttpRequest request) {
        Map<String, String> parameters = getBody(request);
        String userId = parameters.get("userId");
        String password = parameters.get("password");
        logger.debug("Login request for user {} with password {}", userId, password);

        Optional<User> findUser = userDao.findByUserId(userId);
        boolean matches = findUser.isPresent() && findUser.get().matchesPassword(password);

        StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
        Headers headers = new Headers();
        if (matches) {
            String sid = sessionStorage.store(findUser.get());
            headers.addHeader("Location", "/index.html");
            headers.addHeader("Set-Cookie", "sid=" + sid + "; Path=/");
        } else {
            headers.addHeader("Location", "/user/login_failed.html");
        }
        return new HttpResponse(statusLine, headers, null);
    }

    @PostMapping(path = "/logout")
    public HttpResponse logout(HttpRequest request) {
        String sessionId = getSessionId(request);
        if(sessionId != null) {
            sessionStorage.invalidate(sessionId);
        }

        StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
        Headers headers = new Headers();
        headers.addHeader("Set-Cookie", "sid=" + " " + "; Path=/" + "; Max-age=0");
        headers.addHeader("Location", "/index.html");
        return new HttpResponse(statusLine, headers, null);
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

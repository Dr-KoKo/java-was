package codesquad.application.handler;

import codesquad.annotation.api.GetMapping;
import codesquad.annotation.api.PostMapping;
import codesquad.annotation.api.parameter.FormData;
import codesquad.annotation.api.parameter.SessionAttribute;
import codesquad.application.model.User;
import codesquad.application.returnvaluehandler.ModelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.header.Header;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

import java.util.*;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final UserDao userDao;
    private final SessionStorage sessionStorage;

    public RequestHandler(UserDao userDao, SessionStorage sessionStorage) {
        this.userDao = userDao;
        this.sessionStorage = sessionStorage;
    }

    @GetMapping(path = "/")
    public ModelView index(@SessionAttribute(required = false) User user) {
        return new ModelView("/templates/index.html", user == null ? Collections.emptyMap() : Map.of("user", user));
    }

    @GetMapping(path = "/user/registration")
    public ModelView createUser(@SessionAttribute(required = false) User user, HttpRequest request) {
        return new ModelView("/templates/user/registration/index.html", user == null ? Collections.emptyMap() : Map.of("user", user));
    }

    @GetMapping(path = "/user/login")
    public ModelView loginForm(@SessionAttribute(required = false) User user, HttpRequest request) {
        String status = request.getQueryString().get("status");
        String path = Objects.equals(status, "fail") ?
                "/templates/user/login/login_failed.html" : "/templates/user/login/index.html";
        return new ModelView(path, user == null ? Collections.emptyMap() : Map.of("user", user));
    }

    @GetMapping(path = "/user/list")
    public ModelView userList(@SessionAttribute User user, HttpRequest request) {
        List<User> all = userDao.findAll();
        return new ModelView("/templates/user/list/index.html", Map.of("user", user, "users", all));
    }

    @PostMapping(path = "/user/login")
    public HttpResponse login(@FormData Map<String, String> parameters) {
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
    public HttpResponse create(@FormData Map<String, String> parameters) {
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

    @GetMapping(path = "/article")
    public ModelView article(@SessionAttribute User user) {
        return new ModelView("/templates/article/index.html", Map.of("user", user));
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
}

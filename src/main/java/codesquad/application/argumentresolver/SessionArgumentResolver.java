package codesquad.application.argumentresolver;

import codesquad.annotation.api.parameter.SessionAttribute;
import codesquad.application.handler.SessionStorage;
import codesquad.application.model.User;
import server.exception.UnauthorizedException;
import server.http.model.HttpRequest;
import server.http.model.header.Header;

import java.lang.reflect.Parameter;

public class SessionArgumentResolver implements ArgumentResolver<User> {
    private final SessionStorage sessionStorage;

    public SessionArgumentResolver(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    @Override
    public User resolve(Parameter parameter, HttpRequest request) {
        SessionAttribute annotation = parameter.getAnnotation(SessionAttribute.class);
        String sessionId = getSessionId(request);
        User session = (User) sessionStorage.get(sessionId);
        if (annotation.required() && session == null) {
            throw new UnauthorizedException("Session not found");
        }
        return session;
    }

    @Override
    public boolean support(Parameter parameter, HttpRequest request) {
        return parameter.isAnnotationPresent(SessionAttribute.class) && parameter.getType().isAssignableFrom(User.class);
    }

    private String getSessionId(HttpRequest request) {
        String cookieValue = request.getHeader().get(Header.COOKIE.getFieldName());
        if (cookieValue == null) {
            return null;
        }
        for (String cookie : cookieValue.split(";")) {
            if ("sid".equals(cookie.split("=")[0])) {
                return cookie.split("=")[1];
            }
        }
        return null;
    }
}

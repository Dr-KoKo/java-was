package codesquad.application.handler;

import codesquad.annotation.PostMapping;
import codesquad.application.model.User;
import codesquad.application.parser.BodyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;

import java.util.Map;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final BodyParser parser;

    public RequestHandler(BodyParser parser) {
        this.parser = parser;
    }

    @PostMapping(path = "/create")
    public HttpResponse create(HttpRequest request) {
        Map<String, String> parameters = parser.parse(new String(request.getBody().getMessage()));
        for (Map.Entry<String, String> stringStringEntry : parameters.entrySet()) {
            logger.debug("Parameter name: {} value: {}", stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        // store user
        User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("nickname"));
        logger.debug("Creating request processor for user {}", user);

        StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
        Headers headers = new Headers();
        headers.addHeader("Location", "/index.html");
        return new HttpResponse(statusLine, headers, null);
    }
}

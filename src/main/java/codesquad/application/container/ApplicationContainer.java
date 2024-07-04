package codesquad.application.container;

import codesquad.application.user.model.User;
import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;
import codesquad.http.model.header.Headers;
import codesquad.http.model.startline.Method;
import codesquad.http.model.startline.StatusCode;
import codesquad.http.model.startline.StatusLine;
import codesquad.http.model.startline.Version;
import codesquad.server.processor.ActionProcessors;
import codesquad.server.processor.ActionProcessors.RequestMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationContainer {
    private static ApplicationContainer instance;

    private final ActionProcessors actionProcessors;

    private ApplicationContainer(ActionProcessors actionProcessors) {
        this.actionProcessors = actionProcessors;
    }

    public static ApplicationContainer getInstance() {
        if (instance == null) {
            Map<RequestMap, Supplier<Function<HttpRequest, HttpResponse>>> processors = new HashMap<>();
            processors.put(new RequestMap(Method.GET, "/create"), () -> createHttpRequestProcessor());
            ActionProcessors actionProcessors = new ActionProcessors(processors);
            instance = new ApplicationContainer(actionProcessors);
        }
        return instance;
    }

    public ActionProcessors actionProcessors() {
        return actionProcessors;
    }

    private static Function<HttpRequest, HttpResponse> createHttpRequestProcessor() {
        return httpRequest -> {
            Map<String, String> queryString = httpRequest.getQueryString();
            // store user
            User user = new User(queryString.get("userId"), queryString.get("userName"), queryString.get("password"));
            System.out.println(user);

            StatusLine statusLine = new StatusLine(Version.HTTP_1_1, StatusCode.FOUND);
            Headers headers = new Headers();
            headers.addHeader("Location", "/login");
            return new HttpResponse(statusLine, headers, null);
        };
    }
}

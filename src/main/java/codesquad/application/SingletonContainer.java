package codesquad.application;

import codesquad.application.handler.RequestHandler;
import codesquad.application.parser.FormDataBodyParser;

public class SingletonContainer {
    private static SingletonContainer instance;

    private final RequestHandler requestHandler;

    private SingletonContainer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public static SingletonContainer getInstance() {
        if (instance == null) {
            instance = new SingletonContainer(new RequestHandler(new FormDataBodyParser()));
        }
        return instance;
    }

    public RequestHandler requestHandler() {
        return requestHandler;
    }
}

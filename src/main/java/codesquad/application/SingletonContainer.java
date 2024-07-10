package codesquad.application;

import codesquad.application.handler.RequestHandler;
import codesquad.application.parser.FormDataBodyParser;
import codesquad.infra.MemorySessionStorage;
import codesquad.infra.MemoryStorage;

public class SingletonContainer {
    private static SingletonContainer instance;

    private final RequestHandler requestHandler;

    private SingletonContainer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public static SingletonContainer getInstance() {
        if (instance == null) {
            instance = new SingletonContainer(new RequestHandler(new FormDataBodyParser(), new MemoryStorage(), new MemorySessionStorage()));
        }
        return instance;
    }

    public RequestHandler requestHandler() {
        return requestHandler;
    }
}

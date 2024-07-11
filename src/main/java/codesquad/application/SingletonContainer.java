package codesquad.application;

import codesquad.application.handler.RequestHandler;
import codesquad.application.parser.FormDataBodyParser;
import codesquad.infra.MemorySessionStorage;
import codesquad.infra.MemoryStorage;

public class SingletonContainer {
    private static SingletonContainer instance;

    private RequestHandler requestHandler;

    private SingletonContainer() {
    }

    public static SingletonContainer getInstance() {
        if (instance == null) {
            instance = new SingletonContainer();
        }
        return instance;
    }

    public RequestHandler requestHandler() {
        if (requestHandler == null) {
            requestHandler = new RequestHandler(new FormDataBodyParser(), new MemoryStorage(), new MemorySessionStorage());
        }
        return requestHandler;
    }
}

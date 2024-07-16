package codesquad.application;

import codesquad.application.adapter.RequestHandlerAdapter;
import codesquad.application.argumentresolver.ArgumentResolver;
import codesquad.application.argumentresolver.FormUrlEncodedResolver;
import codesquad.application.argumentresolver.NoOpArgumentResolver;
import codesquad.application.handler.RequestHandler;
import codesquad.infra.MemorySessionStorage;
import codesquad.infra.MemoryStorage;

import java.util.List;

public class SingletonContainer {
    private static SingletonContainer instance;

    private RequestHandler requestHandler;
    private RequestHandlerAdapter requestHandlerAdapter;
    private List<ArgumentResolver<?>> argumentResolvers;

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
            requestHandler = new RequestHandler(new MemoryStorage(), new MemorySessionStorage());
        }
        return requestHandler;
    }

    public RequestHandlerAdapter requestHandlerAdapter() {
        if (requestHandlerAdapter == null) {
            requestHandlerAdapter = new RequestHandlerAdapter(argumentResolvers());
        }
        return requestHandlerAdapter;
    }

    public List<ArgumentResolver<?>> argumentResolvers() {
        if (argumentResolvers == null) {
            argumentResolvers = List.of(new NoOpArgumentResolver(), new FormUrlEncodedResolver());
        }
        return argumentResolvers;
    }
}

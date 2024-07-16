package codesquad.application;

import codesquad.application.adapter.RequestHandlerAdapter;
import codesquad.application.argumentresolver.ArgumentResolver;
import codesquad.application.argumentresolver.FormUrlEncodedResolver;
import codesquad.application.argumentresolver.NoOpArgumentResolver;
import codesquad.application.argumentresolver.SessionArgumentResolver;
import codesquad.application.handler.RequestHandler;
import codesquad.application.handler.SessionStorage;
import codesquad.infra.MemorySessionStorage;
import codesquad.infra.MemoryStorage;

import java.util.List;

public class SingletonContainer {
    private static SingletonContainer instance;

    private RequestHandler requestHandler;
    private RequestHandlerAdapter requestHandlerAdapter;
    private List<ArgumentResolver<?>> argumentResolvers;
    private SessionStorage sessionStorage;

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
            requestHandler = new RequestHandler(new MemoryStorage(), sessionStorage());
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
            argumentResolvers = List.of(new NoOpArgumentResolver(), new FormUrlEncodedResolver(), new SessionArgumentResolver(sessionStorage()));
        }
        return argumentResolvers;
    }

    public SessionStorage sessionStorage() {
        if (sessionStorage == null) {
            sessionStorage = new MemorySessionStorage();
        }
        return sessionStorage;
    }
}

package codesquad.application;

import codesquad.application.adapter.RequestHandlerAdapter;
import codesquad.application.argumentresolver.*;
import codesquad.application.handler.RequestHandler;
import codesquad.application.handler.SessionStorage;
import codesquad.application.returnvaluehandler.ModelViewHandler;
import codesquad.application.returnvaluehandler.NoOpViewHandler;
import codesquad.application.returnvaluehandler.ReturnValueHandler;
import codesquad.infra.JdbcArticleStorage;
import codesquad.infra.JdbcUserStorage;
import codesquad.infra.MemorySessionStorage;

import java.util.List;

public class SingletonContainer {
    private static SingletonContainer instance;

    private RequestHandler requestHandler;
    private RequestHandlerAdapter requestHandlerAdapter;
    private List<ArgumentResolver<?>> argumentResolvers;
    private List<ReturnValueHandler> returnValueHandlers;
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
            requestHandler = new RequestHandler(new JdbcUserStorage(), new JdbcArticleStorage(), sessionStorage());
        }
        return requestHandler;
    }

    public RequestHandlerAdapter requestHandlerAdapter() {
        if (requestHandlerAdapter == null) {
            requestHandlerAdapter = new RequestHandlerAdapter(argumentResolvers(), returnValueHandlers());
        }
        return requestHandlerAdapter;
    }

    public List<ArgumentResolver<?>> argumentResolvers() {
        if (argumentResolvers == null) {
            argumentResolvers = List.of(new NoOpArgumentResolver(), new FormUrlEncodedResolver(), new MultipartArgumentResolver(), new SessionArgumentResolver(sessionStorage()));
        }
        return argumentResolvers;
    }

    public List<ReturnValueHandler> returnValueHandlers() {
        if (returnValueHandlers == null) {
            returnValueHandlers = List.of(new NoOpViewHandler(), new ModelViewHandler());
        }
        return returnValueHandlers;
    }

    public SessionStorage sessionStorage() {
        if (sessionStorage == null) {
            sessionStorage = new MemorySessionStorage();
        }
        return sessionStorage;
    }
}

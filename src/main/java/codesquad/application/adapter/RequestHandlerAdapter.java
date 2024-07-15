package codesquad.application.adapter;

import codesquad.application.argumentresolver.ArgumentResolver;
import codesquad.application.handler.RequestHandler;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestHandlerAdapter {
    private final List<ArgumentResolver<?>> argumentResolvers = new ArrayList<>();

    public RequestHandlerAdapter(List<ArgumentResolver<?>> argumentResolvers) {
        this.argumentResolvers.addAll(argumentResolvers);
    }

    public HttpResponse handle(RequestHandler target, Method method, HttpRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            arguments[i] = resolve(parameter, request);
            if (arguments[i] == null) {
                throw new UnsupportedOperationException("unsupported parameter: " + parameter);
            }
        }
        try {
            return (HttpResponse) method.invoke(target, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object resolve(Parameter parameter, HttpRequest request) {
        for (ArgumentResolver<?> resolver : argumentResolvers) {
            if (resolver.support(parameter)) {
                return resolver.resolve(request);
            }
        }
        return null;
    }
}

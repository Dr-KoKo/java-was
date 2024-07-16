package codesquad.application.adapter;

import codesquad.application.argumentresolver.ArgumentResolver;
import codesquad.application.handler.RequestHandler;
import codesquad.application.returnvaluehandler.ReturnValueHandler;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestHandlerAdapter {
    private final List<ArgumentResolver<?>> argumentResolvers = new ArrayList<>();
    private final List<ReturnValueHandler> returnValueHandlers = new ArrayList<>();

    public RequestHandlerAdapter(List<ArgumentResolver<?>> argumentResolvers, List<ReturnValueHandler> returnValueHandlers) {
        this.argumentResolvers.addAll(argumentResolvers);
        this.returnValueHandlers.addAll(returnValueHandlers);
    }

    public HttpResponse handle(RequestHandler target, Method method, HttpRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveArgument(parameters[i], request);
        }
        try {
            Object result = method.invoke(target, arguments);
            return handleReturnValue(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse handleReturnValue(Object result) {
        for (ReturnValueHandler handler : returnValueHandlers) {
            if (handler.support(result)) {
                return handler.handle(result);
            }
        }
        throw new UnsupportedOperationException("No Handler found for result" + result);
    }

    private Object resolveArgument(Parameter parameter, HttpRequest request) {
        for (ArgumentResolver<?> resolver : argumentResolvers) {
            if (resolver.support(parameter, request)) {
                return resolver.resolve(parameter, request);
            }
        }
        throw new UnsupportedOperationException("No resolver found for parameter " + parameter.getName());
    }
}

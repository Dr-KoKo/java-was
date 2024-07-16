package codesquad.application.argumentresolver;

import server.http.model.HttpRequest;

import java.lang.reflect.Parameter;

public class NoOpArgumentResolver implements ArgumentResolver<HttpRequest> {
    @Override
    public HttpRequest resolve(Parameter parameter, HttpRequest request) {
        return request;
    }

    @Override
    public boolean support(Parameter parameter, HttpRequest request) {
        return parameter.getType().isAssignableFrom(HttpRequest.class);
    }
}

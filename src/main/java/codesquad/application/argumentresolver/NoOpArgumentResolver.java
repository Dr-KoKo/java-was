package codesquad.application.argumentresolver;

import server.http.model.HttpRequest;

import java.lang.reflect.Parameter;

public class NoOpArgumentResolver implements ArgumentResolver<HttpRequest> {
    @Override
    public HttpRequest resolve(HttpRequest request) {
        return request;
    }

    @Override
    public boolean support(Parameter parameter) {
        return HttpRequest.class.isAssignableFrom(parameter.getType());
    }
}

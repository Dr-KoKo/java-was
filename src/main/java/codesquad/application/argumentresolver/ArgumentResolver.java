package codesquad.application.argumentresolver;

import server.http.model.HttpRequest;

import java.lang.reflect.Parameter;

public interface ArgumentResolver<T> {
    T resolve(Parameter parameter, HttpRequest request);

    boolean support(Parameter parameter, HttpRequest request);
}

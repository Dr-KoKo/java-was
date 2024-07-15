package codesquad.application.argumentresolver;

import server.http.model.HttpRequest;

import java.lang.reflect.Parameter;

public interface ArgumentResolver<T> {
    T resolve(HttpRequest request);

    boolean support(Parameter parameter);
}

package codesquad.server.processor;

import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;
import codesquad.http.model.startline.Method;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ActionProcessors implements HttpRequestProcessor {
    private final Map<RequestMap, Supplier<Function<HttpRequest, HttpResponse>>> handlerMap;

    public ActionProcessors(Map<RequestMap, Supplier<Function<HttpRequest, HttpResponse>>> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        Supplier<Function<HttpRequest, HttpResponse>> processorSupplier = handlerMap.get(new RequestMap(request.getMethod(), request.getRequestPath()));
        if (processorSupplier == null) {
            return null;
        }
        return processorSupplier.get().apply(request);
    }

    @Override
    public boolean supports(HttpRequest request) {
        return handlerMap.containsKey(new RequestMap(request.getMethod(), request.getRequestPath()));
    }

    public record RequestMap(
            Method method,
            String requestPath
    ) {
    }
}

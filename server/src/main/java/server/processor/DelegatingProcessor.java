package server.processor;


import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DelegatingProcessor implements HttpRequestProcessor {
    private static DelegatingProcessor instance;

    private final Map<RequestMap, Function<HttpRequest, HttpResponse>> handlerMap;

    private DelegatingProcessor(Map<RequestMap, Function<HttpRequest, HttpResponse>> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public static DelegatingProcessor getInstance() {
        if (instance == null) {
            instance = new DelegatingProcessor(new HashMap<>());
        }
        return instance;
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        Function<HttpRequest, HttpResponse> processor = handlerMap.get(new RequestMap(request.getMethod(), request.getRequestPath()));
        if (processor == null) {
            return null;
        }
        return processor.apply(request);
    }

    @Override
    public boolean supports(HttpRequest request) {
        return handlerMap.containsKey(new RequestMap(request.getMethod(), request.getRequestPath()));
    }

    public void addRequestMappings(Map<RequestMap, Function<HttpRequest, HttpResponse>> processorSuppliers) {
        handlerMap.putAll(processorSuppliers);
    }

    public void addRequestMapping(RequestMap requestMap, Function<HttpRequest, HttpResponse> processorSupplier) {
        handlerMap.put(requestMap, processorSupplier);
    }

    public record RequestMap(
            Method method,
            String requestPath
    ) {
    }
}

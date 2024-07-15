package server.processor;


import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

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
        for (RequestMap requestMap : handlerMap.keySet()) {
            if (requestMap.matches(request)) {
                return handlerMap.get(requestMap).apply(request);
            }
        }
        return null;
    }

    @Override
    public boolean supports(HttpRequest request) {
        for (RequestMap requestMap : handlerMap.keySet()) {
            if (requestMap.matches(request)) {
                return true;
            }
        }
        return false;
    }

    public void addRequestMappings(Map<RequestMap, Function<HttpRequest, HttpResponse>> processorSuppliers) {
        handlerMap.putAll(processorSuppliers);
    }

    public void addRequestMapping(RequestMap requestMap, Function<HttpRequest, HttpResponse> processorSupplier) {
        handlerMap.put(requestMap, processorSupplier);
    }

    public record RequestMap(
            Method method,
            Pattern requestPath
    ) {
        public boolean matches(HttpRequest request) {
            return request.getMethod() == method && requestPath.matcher(request.getRequestPath()).matches();
        }
    }
}

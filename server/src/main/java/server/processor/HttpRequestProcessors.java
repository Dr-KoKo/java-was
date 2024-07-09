package server.processor;


import server.http.model.HttpRequest;
import server.http.model.HttpResponse;

import java.util.List;

public class HttpRequestProcessors implements HttpRequestProcessor {
    private final List<HttpRequestProcessor> processors;

    public HttpRequestProcessors() {
        this.processors = List.of(DelegatingProcessor.getInstance(), new GetStaticResourceProcessor());
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        HttpResponse response;
        for (HttpRequestProcessor processor : processors) {
            if (processor.supports(request)) {
                response = processor.process(request);
                if (response != null) {
                    return response;
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}

package server.processor;


import server.exception.ResponseException;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.StatusCode;

import java.util.List;

public class HttpRequestProcessors implements HttpRequestProcessor {
    private final List<HttpRequestProcessor> processors;

    public HttpRequestProcessors() {
        this.processors = List.of(DelegatingProcessor.getInstance(), new GetStaticResourceProcessor());
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        boolean found = false;
        for (HttpRequestProcessor processor : processors) {
            if (processor.matches(request)) {
                found = true;
                HttpResponse response = processor.process(request);
                if (response != null) {
                    return response;
                }
            }
        }
        if (found) {
            throw new ResponseException("Method not allowed", StatusCode.Method_Not_Allowed);
        } else {
            throw new ResponseException("Not found", StatusCode.NOT_FOUND);
        }
    }

    @Override
    public boolean matches(HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}

package codesquad.server.processor;

import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;

import java.util.List;

public class HttpRequestProcessors implements HttpRequestProcessor {
    private final List<HttpRequestProcessor> processors;

    public HttpRequestProcessors() {
        this.processors = List.of(new GetStaticResourceProcessor());
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

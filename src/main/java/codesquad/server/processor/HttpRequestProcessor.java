package codesquad.server.processor;

import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;

public interface HttpRequestProcessor {
    HttpResponse process(HttpRequest request);

    boolean supports(HttpRequest request);
}

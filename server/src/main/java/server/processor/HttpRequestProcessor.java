package server.processor;


import server.http.model.HttpRequest;
import server.http.model.HttpResponse;

public interface HttpRequestProcessor {
    HttpResponse process(HttpRequest request);

    boolean supports(HttpRequest request);
}

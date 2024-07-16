package codesquad.application.returnvaluehandler;

import server.http.model.HttpResponse;

public interface ReturnValueHandler {
    HttpResponse handle(Object returnValue);

    boolean support(Object returnValue);
}

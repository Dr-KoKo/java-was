package codesquad.application.returnvaluehandler;

import server.http.model.HttpResponse;

public class NoOpViewHandler implements ReturnValueHandler {
    @Override
    public HttpResponse handle(Object returnValue) {
        return (HttpResponse) returnValue;
    }

    @Override
    public boolean support(Object returnValue) {
        return HttpResponse.class.isAssignableFrom(returnValue.getClass());
    }
}

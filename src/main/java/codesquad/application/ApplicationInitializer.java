package codesquad.application;

import codesquad.annotation.GetMapping;
import codesquad.annotation.PostMapping;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;
import server.processor.DelegatingProcessor;
import server.processor.DelegatingProcessor.RequestMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class ApplicationInitializer {
    private ApplicationInitializer() {
    }

    public static void initialize() {
        SingletonContainer container = SingletonContainer.getInstance();

        Map<RequestMap, Function<HttpRequest, HttpResponse>> processors = new HashMap<>();
        java.lang.reflect.Method[] methods = container.requestHandler().getClass().getMethods();
        for (java.lang.reflect.Method method : methods) {
            Stream.of(method.getDeclaredAnnotations())
                    .forEach(annotation -> processAnnotation(annotation, method, container, processors));
        }

        DelegatingProcessor.getInstance().addRequestMappings(processors);
    }

    private static void processAnnotation(Annotation annotation, java.lang.reflect.Method method,
                                          SingletonContainer container,
                                          Map<RequestMap, Function<HttpRequest, HttpResponse>> processors) {
        if (annotation instanceof GetMapping) {
            registerProcessor(Method.GET, ((GetMapping) annotation).path(), method, container, processors);
        } else if (annotation instanceof PostMapping) {
            registerProcessor(Method.POST, ((PostMapping) annotation).path(), method, container, processors);
        }
    }

    private static void registerProcessor(Method httpMethod, String path, java.lang.reflect.Method method,
                                          SingletonContainer container,
                                          Map<RequestMap, Function<HttpRequest, HttpResponse>> processors) {
        processors.put(new RequestMap(httpMethod, path), (httpRequest) -> {
            try {

                return (HttpResponse) method.invoke(container.requestHandler(), httpRequest);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

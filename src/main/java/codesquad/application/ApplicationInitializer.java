package codesquad.application;

import codesquad.annotation.GetMapping;
import codesquad.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;
import server.processor.DelegatingProcessor;
import server.processor.DelegatingProcessor.RequestMap;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ApplicationInitializer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);

    private ApplicationInitializer() {
    }

    public static void initialize() {
        logger.info("Initializing application");
        SingletonContainer container = SingletonContainer.getInstance();

        logger.info("Initializing RequestHandler");
        Map<RequestMap, Function<HttpRequest, HttpResponse>> processors = new HashMap<>();
        java.lang.reflect.Method[] methods = container.requestHandler().getClass().getMethods();
        for (java.lang.reflect.Method method : methods) {
            Stream.of(method.getDeclaredAnnotations())
                    .forEach(annotation -> processAnnotation(annotation, method, container, processors));
        }
        logger.info("Initializing DelegatingProcessor");
        DelegatingProcessor.getInstance().addRequestMappings(processors);
        logger.info("Initializing done");
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
        processors.put(new RequestMap(httpMethod, Pattern.compile(path)),
                (request) -> container.requestHandlerAdapter().handle(container.requestHandler(), method, request));
    }
}

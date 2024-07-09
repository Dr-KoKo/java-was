package codesquad;

import codesquad.application.ApplicationInitializer;
import server.FixedThreadWebServer;

public class Main {
    public static void main(String[] args) {
        run(Main.class, args);
    }

    private static void run(Class<?> clazz, String[] args) {
        ApplicationInitializer.initialize();
        FixedThreadWebServer server = new FixedThreadWebServer(8080, 20);
        server.start();
    }
}

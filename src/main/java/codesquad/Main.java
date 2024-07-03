package codesquad;

import codesquad.server.FixedThreadWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        FixedThreadWebServer server = new FixedThreadWebServer(8080, 20);
        run(server);
    }

    private static void run(FixedThreadWebServer server) {
        server.start();
    }
}

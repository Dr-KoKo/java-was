package server;

public class Main {
    public static void main(String[] args) {
        FixedThreadWebServer server = new FixedThreadWebServer(8080, 20);
        run(server);
    }

    private static void run(FixedThreadWebServer server) {
        server.start();
    }
}

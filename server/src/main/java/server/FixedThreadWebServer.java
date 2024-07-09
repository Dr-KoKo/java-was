package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.exception.ServerException;
import server.http.handler.HttpRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadWebServer {
    private static final Logger logger = LoggerFactory.getLogger(FixedThreadWebServer.class);

    private final int port;
    private final ExecutorService executorService;

    public FixedThreadWebServer(int port, int nThreads) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Listening for connection on port 8080 ....");
            while (true) {
                executorService.execute(new HttpRequestHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}

package codesquad.server.connection;

import codesquad.exception.ConnectedSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public abstract class ConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    protected final Socket socket;
    protected final KeepAliveManager keepAliveManager;

    private BufferedReader in;

    protected ConnectionManager(Socket socket, KeepAliveManager keepAliveManager) {
        this.socket = socket;
        this.keepAliveManager = keepAliveManager;
    }

    public boolean isAlive() {
        if (socket.isOutputShutdown()) {
            logger.info("Socket is closed");
            return false;
        }
        return keepAliveManager.isAlive() && !keepAliveManager.isTimeout();
    }

    public void close() {
        try {
            logger.info("Closing socket");
            socket.close();
        } catch (IOException e) {
            throw new ConnectedSocketException(e);
        }
    }

    public String readLine() {
        try {
            return getInputStreamBufferedReader().readLine();
        } catch (IOException e) {
            throw new ConnectedSocketException(e);
        }
    }

    public String readLines() {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = getInputStreamBufferedReader();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public byte[] readNBytes(int bytes) {
        try {
            return socket.getInputStream().readNBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] response) {
        try {
            socket.getOutputStream().write(response);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new ConnectedSocketException(e);
        }
    }

    public void incrementRequestCounter() {
        keepAliveManager.incrementRequest();
    }

    public BufferedReader getInputStreamBufferedReader() {
        if (in == null) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new ConnectedSocketException(e);
            }
        }
        return in;
    }
}

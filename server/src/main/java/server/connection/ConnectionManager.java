package server.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.exception.ConnectedSocketException;
import server.exception.ResponseException;
import server.http.model.startline.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public abstract class ConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    protected final Socket socket;
    protected final KeepAliveManager keepAliveManager;

    private volatile InputStream is;

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
            return readLine(getInputStream());
        } catch (IOException e) {
            throw new ConnectedSocketException(e);
        }
    }

    public String readUntilCrlf() {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            InputStream inputStream = getInputStream();
            while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public byte[] readNBytes(int bytes) {
        try {
            byte[] result = new byte[bytes];
            int read = getInputStream().readNBytes(result, 0, bytes);
            if (read != bytes) {
                throw new ResponseException("not read", StatusCode.INTERNAL_SERVER_ERROR);
            }
            return result;
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

    private synchronized InputStream getInputStream() {
        if (is == null) {
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                throw new ConnectedSocketException(e);
            }
        }
        return is;
    }

    private String readLine(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = inputStream.read()) != -1) {
            if (c == '\n') {
                break;
            }
            if (c != '\r') {
                sb.append((char) c);
            }
        }
        if (sb.isEmpty() && c == -1) {
            return null;
        }
        return sb.toString();
    }
}

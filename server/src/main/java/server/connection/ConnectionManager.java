package server.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.exception.ConnectedSocketException;

import java.io.*;
import java.net.Socket;

public abstract class ConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    protected final Socket socket;
    protected final KeepAliveManager keepAliveManager;

    private volatile InputStream is;
    private volatile BufferedReader in;

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

    public String readStartLine() {
        try {
            return getInputStreamBufferedReader().readLine();
        } catch (IOException e) {
            throw new ConnectedSocketException(e);
        }
    }

    public String readHeaders() {
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
            ByteArrayOutputStream output = new ByteArrayOutputStream(bytes);
            BufferedReader reader = getInputStreamBufferedReader();
            for (int i = 0; i < bytes; i++) {
                output.write(reader.read());
            }
            return output.toByteArray();
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

    public synchronized BufferedReader getInputStreamBufferedReader() {
        if (in == null) {
            in = new BufferedReader(new InputStreamReader(getInputStream()));
        }
        return in;
    }
}

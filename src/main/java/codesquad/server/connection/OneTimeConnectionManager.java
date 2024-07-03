package codesquad.server.connection;

import java.net.Socket;

public class OneTimeConnectionManager extends ConnectionManager {
    public OneTimeConnectionManager(Socket socket) {
        super(socket, new NoOpKeepAliveManager());
    }
}

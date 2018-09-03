package org.sid.shootin.communication.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSession extends ChildSession {
    private ServerSocket serverSocket;

    public ServerSession(int port) {
        super(null, 0);

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket waitChild() {
        try {
            setSocket(serverSocket.accept());
            return getChildSocket();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    @Override
    public ChildSession linkServer() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.serverSocket != null)
            this.serverSocket.close();
    }
}

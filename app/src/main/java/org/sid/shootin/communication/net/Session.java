package org.sid.shootin.communication.net;

import java.net.ServerSocket;
import java.net.Socket;

public abstract class Session {
    private OnReceiveLin receiveLin;

    public abstract void sendMessage(Message message);

    public abstract void startRecv();

    public abstract void close();

    public void setOnRevc(OnReceiveLin revc) {
        this.receiveLin = revc;
    }

    public OnReceiveLin getReceiveLin() {
        return receiveLin;
    }

    public interface OnReceiveLin {
        void onRevc(Message message);
    }

}

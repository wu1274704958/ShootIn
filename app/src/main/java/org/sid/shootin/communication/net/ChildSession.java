package org.sid.shootin.communication.net;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChildSession extends Session implements Runnable {
    private String targetIp;
    private int targetPort;
    private Socket childSocket;
    private Thread theThred;
    private OutputStream theOutput;

    public ChildSession(String targetIp, int targetPort) {
        this.targetIp = targetIp;
        this.targetPort = targetPort;
        theThred = new Thread(this);
    }

    public ChildSession linkServer() throws IOException {
        if (this.childSocket != null)
            throw new RuntimeException("the session is not closed");
        this.childSocket = new Socket(targetIp, targetPort);
        return this;
    }

    @Override
    public void sendMessage(final Message message) {
        try {
            theOutput = childSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (ChildSession.this.theOutput) {
                        Message.writeMessage(ChildSession.this.theOutput, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void startRecv() {
        if (this.theThred != null) {
            theThred.interrupt();
        }
        theThred = new Thread(this);
        theThred.start();
    }

    @Override
    public void close() {
        try {
            if (this.childSocket != null)
                if (!this.childSocket.isClosed()) {
                    this.childSocket.close();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getChildSocket() {
        return childSocket;
    }

    public void setSocket(Socket socket) {
        this.close();
        this.childSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream theInput = childSocket.getInputStream();
            while (!childSocket.isClosed()) {
                Message message = Message.readMessage(theInput);
                if (getReceiveLin() != null) {
                    getReceiveLin().onRevc(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(getClass().getName(), "recv is closed");
    }
}

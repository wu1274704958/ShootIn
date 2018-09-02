package org.sid.shootin.communication.net;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Room {
    public static final int
            ROOM_FLAG_CREATE = 2,
            ROOM_FLAG_JOIN = 4,
            ROOM_FLAG_NOTHON = 0;
    private String roomName;
    private static Room instance;
    private ChildInfo me;
    private List<ChildInfo> outhers;
    private ServerSession serverSession;
    private ChildSession childSession;
    private boolean isClose = true;
    private final int flag;
    private OnAddChildLin onAddChildLin;

    private Room(int flag) {
        this.flag = flag;
        outhers = new ArrayList<>();
        onAddChildLin = null;
    }

    public static Room getInstance() {
        return instance;
    }

    public static Room createNewRoom(String roomName, String playerName, int port) {
        synchronized (Room.class) {
            if (!instance.isClose())
                throw new RuntimeException("The room is not closed");
            instance = new Room(ROOM_FLAG_CREATE);
        }
        instance.roomName = roomName;
        instance.serverSession = new ServerSession(port);
        ChildInfo childInfo = new ChildInfo();
        childInfo.name = playerName;
        instance.setMe(childInfo);
        return instance;
    }

    public static Room joinNewRoom(String playerName, String ip, int port) {
        synchronized (Room.class) {
            if (!instance.isClose())
                throw new RuntimeException("The room is not closed");
            instance = new Room(ROOM_FLAG_JOIN);
        }
        instance.childSession = new ChildSession(ip, port);
        ChildInfo childInfo = new ChildInfo();
        childInfo.name = playerName;
        instance.setMe(childInfo);
        return instance;
    }

    public Session getSession() {
        if (this.flag == ROOM_FLAG_CREATE)
            return serverSession;
        else if (this.flag == ROOM_FLAG_JOIN)
            return childSession;
        else return null;
    }

    /**
     * 开始等待
     */
    public void accept() {
        switch (flag) {
            case ROOM_FLAG_CREATE:
                acceptChild();
                break;
            case ROOM_FLAG_JOIN:
                acceptServer();
        }
        this.isClose = false;
    }

    /**
     * 等待服务器响应
     */
    private void acceptServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = childSession.linkServer().getChildSocket();
                    byte[] rep = ("{\"server\":\"ok\",\"roomName\":\"" + roomName + "\",\"playerName\":\"" + getMe().name + "\"}").getBytes();
                    Message respoMessage = Message.createMessage(Message.TYPE_STRING, rep, rep.length);
                    Message.writeMessage(socket.getOutputStream(), respoMessage);

                    Message message = Message.readMessage(socket.getInputStream());
                    if (message == null || message.getType() != Message.TYPE_STRING)
                        if (onAddChildLin != null)
                            onAddChildLin.onAdd(null);
                    byte[] contents;
                    String content = new String((contents = message.getContent()) == null ? new byte[0] : contents);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        roomName = jsonObject.getString("roomName");
                        ChildInfo childInfo = new ChildInfo();
                        childInfo.name = jsonObject.getString("playerName");
                        getOuthers().add(childInfo);

                        if (onAddChildLin != null)
                            onAddChildLin.onAdd(childInfo);
                    } catch (JSONException e) {
                        socket.close();
                        serverSession.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void acceptChild() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = serverSession.waitChild();
                try {
                    Message message = Message.readMessage(socket.getInputStream());
                    if (message == null || message.getType() != Message.TYPE_STRING)
                        if (onAddChildLin != null)
                            onAddChildLin.onAdd(null);
                    byte[] contents;
                    String content = new String((contents = message.getContent()) == null ? new byte[0] : contents);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        ChildInfo childInfo = new ChildInfo();
                        childInfo.name = jsonObject.getString("playerName");
                        getOuthers().add(childInfo);

                        byte[] rep = ("{\"server\":\"ok\",\"roomName\":\"" + roomName + "\",\"playerName\":\"" + getMe().name + "\"}").getBytes();
                        Message respoMessage = Message.createMessage(Message.TYPE_STRING, rep, rep.length);
                        Message.writeMessage(socket.getOutputStream(), respoMessage);
                        if (onAddChildLin != null)
                            onAddChildLin.onAdd(childInfo);
                    } catch (JSONException e) {
                        socket.close();
                        serverSession.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ChildInfo getMe() {
        return me;
    }

    public void setMe(ChildInfo me) {
        this.me = me;
    }

    public List<ChildInfo> getOuthers() {
        return outhers;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isClose() {
        return isClose;
    }

    public void close() {
        if (isClose)
            return;
        Session session = getSession();
        if (session != null)
            session.close();
        this.isClose = true;
    }

    public static class ChildInfo implements Serializable {
        public String name;
        public String addrs;
    }

    public void setOnAddChildLin(OnAddChildLin onAddChildLin) {
        this.onAddChildLin = onAddChildLin;
    }

    public interface OnAddChildLin {
        public void onAdd(ChildInfo childInfo);
    }

}

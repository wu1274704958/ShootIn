package org.sid.shootin.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GameInfo extends RealmObject {
    @PrimaryKey
    private int gid;
    private String win;         //获胜者姓名
    private String transport;   //失败者姓名
    private String score;       //比分
    private String date;        //时间

    public GameInfo() {
        Date date = java.util.Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.date = simpleDateFormat.format(date);
    }

    public GameInfo(int gid, String win, String transport, String score) {
        this();
        this.gid = gid;
        this.win = win;
        this.transport = transport;
        this.score = score;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

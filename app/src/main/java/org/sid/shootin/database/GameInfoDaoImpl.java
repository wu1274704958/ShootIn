package org.sid.shootin.database;

import android.content.Intent;

import org.sid.shootin.entity.GameInfo;

import java.util.UUID;

import io.realm.Realm;

public class GameInfoDaoImpl extends BaseRealmDaoImpl<GameInfo> {

    private static GameInfoDaoImpl instace;

    private GameInfoDaoImpl() {
        super(GameInfo.class);
        this.setRealm(
                Realm.getDefaultInstance()
        );
    }

    public static GameInfoDaoImpl getInstace() {
        if (instace == null)
            instace = new GameInfoDaoImpl();
        return instace;
    }

    public void addNewScore(String win, String fid, String score) {
        GameInfo gameInfo = new GameInfo(UUID.randomUUID().hashCode(), win, fid, score);
        this.insertInto(gameInfo);
    }

    @Override
    public void close() {
        super.close();
        instace = null;
    }
}

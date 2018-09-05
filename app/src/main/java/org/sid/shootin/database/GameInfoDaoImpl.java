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

    public void addNewScore(String your, String hier, int yourscore, int hierscore) {
        GameInfo gameInfo = new GameInfo(UUID.randomUUID().hashCode(), your, hier, yourscore, hierscore);
        this.insertInto(gameInfo);
    }

    @Override
    public void close() {
        super.close();
        instace = null;
    }
}

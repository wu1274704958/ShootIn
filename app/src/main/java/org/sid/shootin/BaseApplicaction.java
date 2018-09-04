package org.sid.shootin;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by lenovo on 2018/9/2.
 */

public class BaseApplicaction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

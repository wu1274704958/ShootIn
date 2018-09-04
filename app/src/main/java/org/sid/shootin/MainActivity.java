package org.sid.shootin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import org.sid.shootin.database.GameInfoDaoImpl;
import org.sid.shootin.entity.GameInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.bt);
//        GameInfoDaoImpl.getInstace().addNewScore("小妹", "阿杜", "2:0");
//        GameInfoDaoImpl.getInstace().addNewScore("小妹", "阿杜", "2:1");
//        GameInfoDaoImpl.getInstace().addNewScore("小妹", "阿杜", "2:0");
//        GameInfoDaoImpl.getInstace().addNewScore("小妹", "阿杜", "2:1");
//        GameInfoDaoImpl.getInstace().addNewScore("小妹", "阿杜", "2:0");
//        GameInfoDaoImpl.getInstace().addNewScore("大哥", "小妹", "2:1");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playname = "小妹";
                Intent it = new Intent(MainActivity.this, M2Activity.class);
                it.putExtra("playname", playname);
                startActivity(it);
            }
        });
    }
}

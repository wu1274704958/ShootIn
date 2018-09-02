package org.sid.shootin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.sid.shootin.communication.net.Util;

public class MainActivity extends AppCompatActivity {

    private Button bt_creat;
    private Button bt_join;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_creat = findViewById(R.id.bt_creat);
        bt_join = findViewById(R.id.bt_join);

        bt_join.setOnClickListener(oc);
        bt_creat.setOnClickListener(oc);
    }

    View.OnClickListener oc = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_creat:
                    alertDialog = mydialog(MainActivity.this, 1);
                    alertDialog.show();
                    if (Util.openWifiAp(MainActivity.this, "ShootIn")) {

                    } else {
                        Toast.makeText(MainActivity.this, "热点开启失败，请手动开始", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bt_join:
                    alertDialog = mydialog(MainActivity.this, 0);
                    alertDialog.show();
                    break;
            }
        }
    };

    private AlertDialog mydialog(Context context, int i) {
        builder = new AlertDialog.Builder(context);
        if (i == 1) {
            builder.setView(R.layout.activity_room_waiting);
            return builder.create();
        }else if(i == 0){

            builder.setView(R.layout.activity_room_join);
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            return builder.create();
        }else{

            return builder.create();
        }
    }
}

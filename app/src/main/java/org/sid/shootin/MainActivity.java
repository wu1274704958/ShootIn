package org.sid.shootin;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sid.shootin.communication.net.Room;
import org.sid.shootin.communication.net.Util;

public class MainActivity extends AppCompatActivity {

    private Button bt_creat;
    private Button bt_join;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView my_name;
    private TextView your_name;
    private EditText et_name;
    private EditText et_ip;
    private ProgressBar pb;

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
                    if (Util.openWifiAp(MainActivity.this, "ShootIn")) {
                        alertDialog = createdialog(MainActivity.this, "player1");
                        Room room = Room.createNewRoom("new", "player1", 8889);
                        room.setOnAddChildLin(new Room.OnAddChildLin() {
                            @Override
                            public void onAdd(Room.ChildInfo childInfo) {
                                your_name.setText(childInfo.name);
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {

                                }
                                GameActivity.gotoPlay(MainActivity.this);
                                finish();
                            }
                        });

                        alertDialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "热点开启失败，请手动开始", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bt_join:
                    alertDialog = joindialog(MainActivity.this);
                    alertDialog.show();
                    break;
            }
        }
    };

    private AlertDialog createdialog(Context context, String myname) {
        builder = new AlertDialog.Builder(context);
        View v = View.inflate(this, R.layout.activity_room_create, null);
        my_name = v.findViewById(R.id.my_name);
        your_name = v.findViewById(R.id.your_name);
        my_name.setText(myname);
        builder.setView(v);
        return builder.create();

    }

    private AlertDialog joindialog(Context context) {
        builder = new AlertDialog.Builder(context);
        View v = View.inflate(context, R.layout.activity_room_join, null);
        et_name = v.findViewById(R.id.et_name);
        et_ip = v.findViewById(R.id.et_ip);
        pb = v.findViewById(R.id.pb);
        builder.setView(v)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n = String.valueOf(et_name.getText());
                        String ip = String.valueOf(et_ip.getText());
                        if (!Util.openWifi(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "WIFI开启失败，请手动开始", Toast.LENGTH_SHORT).show();
                        }
                        if (Util.linkWifi(MainActivity.this, "ShootIn", "")) {
                            Room room = Room.joinNewRoom(n, ip, 8889);
                            room.setOnAddChildLin(new Room.OnAddChildLin() {
                                @Override
                                public void onAdd(Room.ChildInfo childInfo) {

                                }
                            });
                            room.accept();
                        } else {
                            Toast.makeText(MainActivity.this, "WIFI连接失败，请手动连接", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {


                    }
                });
        return builder.create();
    }
}

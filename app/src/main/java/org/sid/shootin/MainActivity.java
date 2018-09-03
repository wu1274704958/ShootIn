package org.sid.shootin;

import android.content.Context;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
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
    private View gotoplayButton;
    Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_creat = findViewById(R.id.bt_creat);
        bt_join = findViewById(R.id.bt_join);

        bt_join.setOnClickListener(oc);
        bt_creat.setOnClickListener(oc);
        handler = new Handler();
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
                            public void onAdd(final Room.ChildInfo childInfo) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        your_name.setText(childInfo.name);
                                        if (gotoplayButton != null)
                                            gotoplayButton.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        });
                        room.accept();
                        alertDialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "热点开启失败，请手动开始", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bt_join:
                    if (!Util.openWifi(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "WIFI开启失败，请手动开始", Toast.LENGTH_SHORT).show();
                    }
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
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        (gotoplayButton = v.findViewById(R.id.gotoPlay))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GameActivity.gotoPlay(MainActivity.this);
                        finish();
                    }
                });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Room.getInstance().close();
            }
        });
        return alertDialog;

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
                        WifiManager wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        DhcpInfo info = wifiManager.getDhcpInfo();
                        String ip = intToIp(info.serverAddress);
                        Log.e("=================", intToIp(info.serverAddress));

                        if (Util.linkWifi(MainActivity.this, "ShootIn", "")) {
                            Room room = Room.joinNewRoom(n, ip, 8889);
                            room.setOnAddChildLin(new Room.OnAddChildLin() {
                                @Override
                                public void onAdd(final Room.ChildInfo childInfo) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (childInfo == null) {
                                                Toast.makeText(MainActivity.this, "无法连接", Toast.LENGTH_SHORT).show();
                                            } else {
                                                GameActivity.gotoPlay(MainActivity.this);
                                            }
                                        }
                                    });


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

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}

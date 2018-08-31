package org.sid.shootin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.sid.shootin.communication.net.Util;

public class MainActivity extends AppCompatActivity{

    private Button bt_creat;
    private Button bt_join;
    private AlertDialog.Builder builder;
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
            switch (view.getId()){
                case R.id.bt_creat:

                    if(Util.openWifiAp(MainActivity.this,"ShootIn")){
                        dialog(MainActivity.this,1);
                    }
                    break;
                case R.id.bt_join:
                    dialog(MainActivity.this,0);
                    break;
            }
        }
    };

    private void dialog(Context context,int i){
        builder = new AlertDialog.Builder(context);
        if(i == 1){
            ProgressBar ba = new ProgressBar(context);
            ba.setIndeterminate(true);
            builder.setView(ba);
            builder.create();
            builder.show();
        }else if(i == 0){
            EditText et = new EditText(context);
            et.setLines(1);
            et.setWidth(200);
            builder.setView(et);
            builder.create();
            builder.show();
        }
    }
}

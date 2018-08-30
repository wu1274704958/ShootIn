package org.sid.shootin;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Button bt_creat;
    private Button bt_join;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_creat = findViewById(R.id.bt_creat);
        bt_join = findViewById(R.id.bt_join);
        tv = findViewById(R.id.tv);

        bt_creat.setOnClickListener(oc);
        bt_join.setOnClickListener(oc);
    }

    View.OnClickListener oc = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_creat:

                    break;
                case R.id.bt_join:

                    break;
            }

        }
    };


}

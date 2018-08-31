package org.sid.shootin.communication.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {
    public static byte[] IntToByteArr(int w) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (w >> (3 * 8));
        bytes[1] = (byte) ((w >> (2 * 8)) & 0x000000ff);
        bytes[2] = (byte) ((w >> 8) & 0x000000ff);
        bytes[3] = (byte) w;
        return bytes;
    }

    //    public static int ByteArrToInt(byte[] bys) {
//        if (bys.length < 4)
//            return 0;
//        return Byte.toUnsignedInt(bys[0]) << 24
//                | Byte.toUnsignedInt(bys[1]) << 16
//                | Byte.toUnsignedInt(bys[2]) << 8
//                | Byte.toUnsignedInt(bys[3]);
//    }
    public static int ByteArrT2Int(byte[] bys) {
        int sum = 0;
        int end = 4;
        byte len = 4;
        for (int i = 0; i < end; i++) {
            int n = ((int) bys[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    public static boolean openWifiAp(Context context, String apName) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (manager != null && manager.isWifiEnabled())
            manager.setWifiEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setWifiApEnabledForAndroidO(context, true);
            return true;
        }


        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = apName;
        wifiConfiguration.preSharedKey = null;
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        try {
            Method method = manager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return (boolean) method.invoke(manager, wifiConfiguration, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(Util.class.getName() + ":", "open wifi err " + e.getCause().toString());
            return false;
        }
    }

    public static void setWifiApEnabledForAndroidO(Context context, boolean isEnable) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        Field iConnMgrField = null;
        try {
            iConnMgrField = connManager.getClass().getDeclaredField("mService");
            iConnMgrField.setAccessible(true);
            Object iConnMgr = iConnMgrField.get(connManager);
            Class<?> iConnMgrClass = Class.forName(iConnMgr.getClass().getName());

            if (isEnable) {
                Method startTethering = iConnMgrClass.getMethod("startTethering", int.class, ResultReceiver.class, boolean.class);
                startTethering.invoke(iConnMgr, 0, null, true);
            } else {
                Method startTethering = iConnMgrClass.getMethod("stopTethering", int.class);
                startTethering.invoke(iConnMgr, 0);
            }

        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

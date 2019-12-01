package com.didichuxing.doraemonkit.util.netstate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author maple on 2019/7/9 15:14.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class NetworkUtil {
    /**
     * 打开网络设置页
     */
    public static void openSetting(Context context, int requestCode) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 判断网络可用性
     *
     * @param context application context
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) return false;
        NetworkInfo[] infos = connMgr.getAllNetworkInfo();
        if (infos != null) {
            for (NetworkInfo info : infos) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取
     * @param context
     * @return
     */
    public static NetType getNetType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return NetType.NONE;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) return NetType.NONE;
        int nType = info.getType();
        if (nType ==ConnectivityManager.TYPE_MOBILE){
            if (info.getExtraInfo().toLowerCase().equals("cnnet")){
                return NetType.CMNET;
            }
            return NetType.CMWAP;
        }else if (nType==ConnectivityManager.TYPE_WIFI){
            return NetType.WIFI;
        }
        return NetType.NONE;
    }
}

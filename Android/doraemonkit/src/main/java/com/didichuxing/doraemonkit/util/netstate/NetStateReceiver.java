package com.didichuxing.doraemonkit.util.netstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @author maple on 2019/7/9 15:12.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class NetStateReceiver extends BroadcastReceiver implements INetwork{
    public static final String TAG = "NetStateReceiver";
    private NetType netType;
    private NetChangeObserver observer;

    public NetStateReceiver() {
        netType =NetType.NONE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent==null||intent.getAction()==null)return;
        if (intent.getAction().equalsIgnoreCase(Constants.ANDROID_NET_CHANGE_ACTION)){
            Log.i(TAG, "onReceive: netChanged");
            netType = NetworkUtil.getNetType(context.getApplicationContext());
            if (NetworkUtil.isNetworkAvailable(context)){
                observer.onConnect(netType);
            }else {
                observer.onDisConnect();
            }
        }
    }

    @Override
    public void register(NetChangeObserver observer,Context context) {
        this.observer =observer;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ANDROID_NET_CHANGE_ACTION);
        context.registerReceiver(this, filter);

    }

    @Override
    public void unRegister(Context context) {
        context.unregisterReceiver(this);
    }
}

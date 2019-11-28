package com.didichuxing.doraemonkit.util.netstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * @author maple on 2019/7/9 17:43.
 * @version v1.0
 * @see 1040441325@qq.com
 * onAvailable 连接后必调用onCapabilitiesChanged()且可能重复调用,
 * 所以使用onCapabilitiesChanged()验证连接,并缓存连接状态,减少重复调用
 * new: 该方案只能判断wifi/其他, 抛弃CMMET类型 一般手机上用于区分wifi/4g,足够.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetWorkCallbackImpl extends ConnectivityManager.NetworkCallback implements INetwork {
    private NetChangeObserver observer;
    private static final String TAG = "NetWorkCallbackImpl";
    private NetType netType = NetType.NONE;

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.d(TAG, "onAvailable: ");
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        netType = NetType.NONE;
        observer.onDisConnect();
        Log.d(TAG, "onLost: ");
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        //正在失去连接,部分强制断开不触发该方法(手动关闭wifi)
        super.onLosing(network, maxMsToLive);
        Log.d(TAG, "onLosing: ");
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                if (netType != NetType.WIFI) {
                    netType = NetType.WIFI;
                    observer.onConnect(netType);
                }

                Log.d(TAG, "onCapabilitiesChanged: wifi");
            } else {
                //其他
                if (netType != NetType.CMWAP) {
                    netType = NetType.CMWAP;
                    observer.onConnect(netType);
                }
                Log.d(TAG, "onCapabilitiesChanged: wap");
            }
        }
    }


    @Override
    public void register(NetChangeObserver observer, Context context) {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return;
        manager.registerNetworkCallback(request, this);

        this.observer = observer;
    }

    @Override
    public void unRegister(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.unregisterNetworkCallback(this);
    }
}

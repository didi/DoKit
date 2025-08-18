package com.example.androidpowercomsumption.utils.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

public class DeviceStateListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenBroadcastReceiver;
    private ScreenStateListener mScreenStateListener;

    public DeviceStateListener(Context context) {
        mContext = context;
        mScreenBroadcastReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 设备屏幕状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                /**
                 * 屏幕亮
                 */
                mScreenStateListener.onScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                /**
                 * 屏幕锁定
                 */
                mScreenStateListener.onScreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                /**
                 * 屏幕解锁了且可以使用
                 */
                mScreenStateListener.onUserPresent();
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                // 开始充电
                mScreenStateListener.onPowerConnected();
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                // 停止充电
                mScreenStateListener.onPowerDisconnected();
            }
        }
    }

    /**
     * 开始监听屏幕开/关状态
     *
     * @param listener
     */
    public void register(ScreenStateListener listener) {
        mScreenStateListener = listener;

        /**
         * 注册屏幕设备开屏/锁屏的状态监听
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(mScreenBroadcastReceiver, filter);

        initScreenState();
    }


    /**
     * 代码启动阶段获取设备屏幕初始状态
     */
    private void initScreenState() {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);

        if (manager.isInteractive()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }


    /**
     * 注销屏幕设备开屏/锁屏的状态监听
     */
    public void unregister() {
        mContext.unregisterReceiver(mScreenBroadcastReceiver);
        mScreenBroadcastReceiver = null;
        mScreenStateListener = null;
    }


    public interface ScreenStateListener {
        /**
         * 此时屏幕已经点亮，但可能是在锁屏状态
         * 比如用户之前锁定了屏幕，按了电源键启动屏幕，则回调此方法
         */
        void onScreenOn();

        /**
         * 屏幕被锁定
         */
        void onScreenOff();

        /**
         * 屏幕解锁且可以正常使用
         */
        void onUserPresent();

        // 充电
        void onPowerConnected();

        // 停止充电
        void onPowerDisconnected();
    }
}
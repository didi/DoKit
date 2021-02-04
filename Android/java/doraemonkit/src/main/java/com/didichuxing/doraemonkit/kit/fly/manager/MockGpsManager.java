package com.didichuxing.doraemonkit.kit.fly.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.model.LatLng;

import java.util.List;

public final class MockGpsManager {
    private static final String TAG = "MockGpsManager";
    private static float sBearing = 0;
    private static float sSpeed = 3;
    private static MockGpsManager sInstance;
    int triedInit;
    private LocationManager mLocationManager;
    private Location mLocation = new Location(LocationManager.GPS_PROVIDER);
    private WgsPointer mLastPoint;
    private OnMessage mOnMessage;
    private boolean mIsAvailable;
    private Handler mHandlerUI;
    private Handler mHandlerWorker;
    private Context mContext;
    private final Runnable laterInitRunnable = new Runnable() {
        @Override
        public void run() {
            mHandlerWorker.removeCallbacks(laterInitRunnable);
            try {
                List<String> providers = mLocationManager.getProviders(true);
                LogUtils.d(TAG, "当前可用的 providers 有:" + providers);
                mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                        false, false, false, false, true, true, true, 0, 5);
                mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                mIsAvailable = true;
            } catch (Exception e) {
                String msg = "请到开发者选项中打开模拟定位\n初始化失败 tried " + triedInit;
                onMessage(msg + e.getMessage());
                if (triedInit < 3) {
                    mHandlerUI.postDelayed(laterInitRunnable, triedInit * 2000);
                    LogUtils.d(TAG, msg);
                }
                disableTestProvider();
                LogUtils.e(TAG, msg, e);
            } finally {
                triedInit++;
            }
        }
    };
    private Runnable mockGpsRunnable = new Runnable() {
        @Override
        public void run() {
            mHandlerWorker.removeCallbacks(mockGpsRunnable);
            teleportWgs(GpsMockManager.getInstance().getLatitude(), GpsMockManager.getInstance().getLongitude());
            if (GpsMockManager.getInstance().isMocking()) {
                mHandlerWorker.postDelayed(this, 3000);
            }
        }
    };

    private MockGpsManager(@NonNull Context context) {
        LogUtils.d(TAG, "模拟路测工具初始化");
        HandlerThread handlerThread = new HandlerThread("MockGpsManager Thread");
        handlerThread.start();
        mHandlerWorker = new Handler(handlerThread.getLooper());
        mHandlerUI = new Handler(Looper.getMainLooper());
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        removeTestProvider();
        mHandlerUI.post(laterInitRunnable);
    }

    public static float getBearing() {
        return sBearing;
    }

    public static void setBearing(float bearing) {
        LogUtils.d(TAG, "⚠️setBearing() called with: bearing = [" + bearing + "]");
        MockGpsManager.sBearing = bearing;
    }

    public static float getSpeed() {
        return sSpeed;
    }

    public static void setSpeed(float speed) {
        LogUtils.d(TAG, "⚠️setSpeed() called with: speed = [" + speed + "]");
        MockGpsManager.sSpeed = speed;
    }

    public static String getSpeedBearingStr() {
        return "sSpeed = [" + sSpeed + "], bearing = [" + sBearing + "]";
    }

    public static MockGpsManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MockGpsManager(context);
        }
        return sInstance;
    }

    public void disableTestProvider() {
        try {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            }
            removeTestProvider();
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
        } catch (Exception ex) {
        }
    }

    public boolean changeMockLoc(@NonNull Context context, double latitude, double longitude) {
        if (longitude > 180 || longitude < -180) {
            return false;
        }
        if (latitude > 90 || latitude < -90) {
            return false;
        }
        GpsMockManager.getInstance().mockLocation(latitude, longitude);
        GpsMockConfig.saveMockLocation(new LatLng(latitude, longitude));
        return true;
    }

    public void start() {
        if (GpsMockManager.getInstance().isMocking()) {
            mHandlerWorker.post(mockGpsRunnable);
        }
    }

    public void stop() {
        mHandlerWorker.removeCallbacks(mockGpsRunnable);
    }

    @SuppressLint("DefaultLocale")
    public void teleportGcj(double lat, double lng) {
        GcjPointer pointer = new GcjPointer(lat, lng);
        WgsPointer pointerWgs = pointer.toWgsPointer();
        LogUtils.d(TAG, String.format("MockGpsManager 请求模拟定位 火星坐标 lat=%g, lng=%g", lat, lng));
        teleportWgs(pointerWgs.getLatitude(), pointerWgs.getLongitude());
    }

    public void teleportWgs(double lat, double lng) {
        if (!mIsAvailable) {
            String msg = "第一、设置我们手机允许模拟定位【系统设置】=》开发者选项=》打开允许模拟位置\n第二、进入位置服务切换成只是用gps确定位置";
            onMessage(msg);
            LogUtils.d(TAG, "⚠️teleportWgs() called with mIsAvailable: lat = [" + lat + "], lng = [" + lng + "], sSpeed = [" + sSpeed + "], bearing = [" + sBearing + "]");
        }
        try {
            WgsPointer pointerWgs = new WgsPointer(lat, lng);

            // 保存输入点数据, 用于校验
            mLastPoint = pointerWgs;
            mLocation.setLatitude(pointerWgs.getLatitude());
            mLocation.setLongitude(pointerWgs.getLongitude());
            mLocation.setAltitude(0);
            mLocation.setAccuracy(5);
            mLocation.setBearing(sBearing);
            mLocation.setSpeed(sSpeed);
            mLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }

            LogUtils.d(TAG, "⚠️teleportWgs() 中 GPS Provider 可用, 设置坐标: lat = [" + lat + "], lng = [" + lng + "], sSpeed = [" + sSpeed + "], bearing = [" + sBearing + "]");
            mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mLocation);
        } catch (SecurityException e) {
            String msg = "请到开发者选项中打开模拟定位";
//            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            onMessage(msg);
        } catch (Exception e) {
            LogUtils.d(TAG, "Provider \"gps\" unknown. 拦截下来了.");
            try {
                removeTestProvider();
                mLocationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                        false, false, false, false, true, true, true, 0, 5);
                mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                teleportWgs(
                        mLastPoint.getLatitude(),
                        mLastPoint.getLongitude()
                );
            } catch (Exception e1) {
                e1.printStackTrace();
                disableTestProvider();
            }
        }
    }

    public void setListener(OnMessage onMessage) {
        mOnMessage = onMessage;
    }

    public void stopMock() {
        disableTestProvider();
    }

    private void removeTestProvider() {
        try {
            LogUtils.d(TAG, "" + "removeTestProvider();Removing Test providers");
            mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (Exception error) {
            LogUtils.i(TAG, "⚠️Got exception in removing test  provider :" + error.toString());
        }
    }

    private void onMessage(String msg) {
        if (mOnMessage != null) {
            mOnMessage.onMessage(msg);
        }
    }

    interface OnMessage {
        void onMessage(String msg);
    }
}

package com.didichuxing.doraemonkit.gps_mock.lbs.route;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;


import com.didichuxing.doraemonkit.gps_mock.lbs.common.LocInfo;
import com.didichuxing.doraemonkit.gps_mock.lbs.manual.FloatGpsMockCache;
import com.didichuxing.doraemonkit.gps_mock.lbs.preset.FloatGpsPresetMockCache;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: changzuozhen
 * @Date: 2019-10-15 15:37
 */
public class FloatGpsRouteMockCache {
    private static final String TAG = "EnvironmentKit";
    private static List<Pair<Double, Double>> sMockRoute;
    private static int sMockRouteIndex = 0;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Timer sTimer;

    public FloatGpsRouteMockCache() {
    }

    public static LocInfo getCurrentLocConfig() {
        return FloatGpsPresetMockCache.getMockLocConfig();
    }

    public static void mockGpsRoute(Context context, List<Pair<Double, Double>> routes) {
        sMockRoute = routes;
        restartPlayMockRoute(context);
        if (sIOnRouteChange != null) {
            sIOnRouteChange.onRouteChange();
        }
    }

    public static void restartPlayMockRoute(Context context) {
        if (sMockRoute == null || sMockRoute.size() == 0) return;
        sMockRouteIndex = 0;
        resumePlayMockRoute(context);
    }

    public static void resumePlayMockRoute(Context context) {
        if (sTimer != null) {
            sTimer.cancel();
        }
        sTimer = new Timer();
        final Context finalContext = context;
        sTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (sMockRoute != null && sMockRoute.size() > sMockRouteIndex) {
                    setMockRouteProgress(finalContext, sMockRouteIndex);
                    sMockRouteIndex++;
                } else {
                    sTimer.cancel();
                }
            }
        }, 0, 500);
    }

    public static void pausePlayMockRoute() {
        if (sTimer != null) {
            sTimer.cancel();
        }
    }

    public static void clearMockRoute() {
        if (sTimer != null) {
            sTimer.cancel();
            sMockRouteIndex = 0;
            if (sMockRoute != null) {
                sMockRoute.clear();
            }
        }
    }

    public static int getMockRouteProgress() {
        return sMockRouteIndex;
    }

    public static int getRouteCount() {
        return sMockRoute == null ? 0 : sMockRoute.size();
    }

    public static int setMockRouteProgress(Context context, int index) {
        if (getRouteCount() > index && index > 0) {
            sMockRouteIndex = index;
            Pair<Double, Double> mockRoutePoint = sMockRoute.get(index);
            FloatGpsMockCache.mockToLocation(mockRoutePoint.first, mockRoutePoint.second);
            if (sIOnRouteChange != null) {
                sIOnRouteChange.onIndexChange(sMockRouteIndex);
            }
        }
        return sMockRouteIndex;
    }

    public interface IOnRouteChange {
        void onRouteChange();

        void onIndexChange(int index);
    }

    private static IOnRouteChange sIOnRouteChange;

    public static void setRouteChangeListener(IOnRouteChange onRouteChange) {
        sIOnRouteChange = onRouteChange;
    }

}

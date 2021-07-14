package com.didichuxing.doraemondemo.amap.mockroute;

import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * 使用Dokit位置mock
 * changzuozhen
 * 2021年 04月 20日
 */
public class MockGPSTaskManager {
    private static final String TAG = "TaskMockManager";
    public static int PERIOD_LEVEL_MIN = 1;
    public static int PERIOD_LEVEL_MAX = 20;
    public static int PERIOD_UNIT = 50;
    private static int mPeriodLevel = PERIOD_LEVEL_MAX;

    private Long mProgressIndex = 0L;
    private Long mProgressIndexMax = 0L;

    private boolean mPause = false;
    private boolean mSkip = false;

    MockGPSTaskData taskInfoData;

    public static MockGPSTaskManager sMockGPSTaskManager;

    public MockGPSTaskManager(MockGPSTaskData mockGPSTaskData) {
        this.taskInfoData = mockGPSTaskData;
        mProgressIndexMax = Long.valueOf(mockGPSTaskData.mockGPSItems.size() - 1);

    }

    public Observable<Location> startGpsMockTask() {
        return Observable.interval(0, PERIOD_UNIT, TimeUnit.MILLISECONDS, Schedulers.io())
//                Observable.interval(0, PERIOD_UNIT * getPeriodLevel(), TimeUnit.MILLISECONDS, Schedulers.io())
//                Observable.timer(PERIOD_UNIT * getPeriodLevel(), TimeUnit.MICROSECONDS)
//                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        boolean periodMatched = (aLong % getPeriodLevel()) == 0;
                        return checkMockLocationState() && periodMatched;
//                        return checkMockLocationState();
                    }
                })
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return mProgressIndex++;
                    }
                })
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return !mSkip && taskInfoData != null && aLong < taskInfoData.mockGPSItems.size();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Location>() {
                    private Location mLastLocation;
                    @Override
                    public Location apply(Long aLong) throws Exception {
                        MockGPSTaskData.MockGPSItem mockGPSItem = taskInfoData.mockGPSItems.get(aLong.intValue());
                        // 轨迹模拟
                        Location location = new Location(LocationManager.GPS_PROVIDER);
                        location.setLatitude(mockGPSItem.lat);
                        location.setLongitude(mockGPSItem.lng);
                        location.setAccuracy(mockGPSItem.accuracy.floatValue());
                        location.setSpeed(mockGPSItem.speed.floatValue());
                        location.setBearing(mockGPSItem.bearing);
                        LogUtils.v(TAG, "⚠️模拟定位：" + location + " index: " + aLong + " TaskInfoData: " + taskInfoData);
                        float calculateLineDistance = (location == null || mLastLocation == null) ? -1 : AMapUtils.calculateLineDistance(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()));
                        if (calculateLineDistance < 0) {
                            LogUtils.v(TAG, "⚠️模拟定位：" + location + " index: " + aLong + " TaskInfoData: " + taskInfoData);
                        } else if (calculateLineDistance > 100) {
                            LogUtils.w(TAG, "⚠️模拟定位： 跳动距离：" + calculateLineDistance + " " + location + " index: " + aLong + " TaskInfoData: " + taskInfoData);
                        } else {
                            LogUtils.v(TAG, "⚠️模拟定位： 跳动距离：" + calculateLineDistance + " " + location + " index: " + aLong + " TaskInfoData: " + taskInfoData);
                        }
                        mLastLocation = location;

                        //GpsMockManager.getInstance().mockLocationWithNotify(location);
                        return location;
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        LogUtils.d(TAG, "mock 子任务异常 " + taskInfoData + " " + throwable + "]");
                    }
                });
    }

    /**
     * 根据高德导航的轨迹数据进行轨迹模拟
     *
     * @param naviRouteInfo
     * @return
     */
    @Nullable
    public static Observable<Location> startGpsMockTask(@Nullable AMapNaviPath naviRouteInfo) {
        if (naviRouteInfo != null && naviRouteInfo.getCoordList() != null) {
            MockGPSTaskData mockGPSTaskData = new MockGPSTaskData();
            mockGPSTaskData.mockGPSItems = new ArrayList();
            Iterator cordListIt = naviRouteInfo.getCoordList().iterator();
            while (cordListIt.hasNext()) {
                NaviLatLng latLng = (NaviLatLng) cordListIt.next();
                MockGPSTaskData.MockGPSItem mockGPSItem = new MockGPSTaskData.MockGPSItem();
                mockGPSTaskData.mockGPSItems.add(mockGPSItem.setLat(latLng.getLatitude()).setLng(latLng.getLongitude()).setTime(System.currentTimeMillis()).setAccuracy(10.0F).setBearing(0L).setSpeed(15.0D));
            }
            MockGPSTaskData.modifyBearing(mockGPSTaskData.mockGPSItems);
            MockGPSTaskManager mockGPSTaskManager = new MockGPSTaskManager(mockGPSTaskData);
            if (sMockGPSTaskManager != null) {
                sMockGPSTaskManager.setSkip(true);
            }
            sMockGPSTaskManager = mockGPSTaskManager;
            return mockGPSTaskManager.startGpsMockTask();
        } else {
            return null;
        }
    }

    /**
     * 暂停当前任务
     */
    public void pause() {
        mPause = true;
    }

    /**
     * 恢复执行当前任务
     */
    public void resume() {
        mPause = false;
    }

    /**
     * 跳过当前任务
     *
     * @param skip
     * @return
     */
    public MockGPSTaskManager setSkip(boolean skip) {
        this.mSkip = skip;
        return this;
    }

    /**
     * 获取时间间隔等级
     *
     * @return
     */
    public static int getPeriodLevel() {
        return mPeriodLevel;
    }

    /**
     * 时间间隔等级
     * 1       --- PERIOD_UNIT * 1      = 50
     * 10      --- PERIOD_UNIT * 10     = 500
     * 20      --- PERIOD_UNIT * 20     = 1000
     */
    public static void setPeriodLevel(int periodLevel) {
        if (periodLevel < 1) {
            mPeriodLevel = 1;
        } else {
            mPeriodLevel = periodLevel;
        }
        LogUtils.v(TAG, "setPeriodLevel() called with: periodLevel = [" + mPeriodLevel + "]");
    }

    public boolean checkMockLocationState() {
//        return !mPause && mState > 5;
        return !mPause;
    }

    public Long getSeekProgress() {
        return mProgressIndex;
    }

    public MockGPSTaskManager seekProgress(Long progress) {
        if (progress < 0) {
            mProgressIndex = 0L;
        } else if (progress > mProgressIndexMax) {
            this.mProgressIndex = mProgressIndexMax;
        } else {
            this.mProgressIndex = progress;
        }
        return this;
    }

    public boolean completed() {
        return mProgressIndex >= mProgressIndexMax;
    }
}

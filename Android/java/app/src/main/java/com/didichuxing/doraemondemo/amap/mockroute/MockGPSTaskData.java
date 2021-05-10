package com.didichuxing.doraemondemo.amap.mockroute;

import java.util.ArrayList;

/**
 * changzuozhen
 * 2021年 04月 20日
 */
public class MockGPSTaskData {
    private static final String TAG = "TaskInfoData";
    public double start_lng;
    public double start_lat;
    public double end_lng;
    public double end_lat;
    public ArrayList<MockGPSItem> mockGPSItems;

    @Override
    public String toString() {
        return "任务 轨迹点=" + mockGPSItems.size() +
                ", start_lat,lng= " + start_lat + "," + start_lng +
                ", end_lat,lng= " + end_lat + "," + end_lng;
    }

    public static class MockGPSItem {
        public Double lng;
        public Double lat;

        public Long time;

        public Float accuracy;
        public Double speed;
        public Long bearing;

        @Override
        public String toString() {
            return "TraceItem{" +
                    "lng=" + lng +
                    ", lat=" + lat +
                    ", time=" + time +
                    ", accuracy=" + accuracy +
                    ", speed=" + speed +
                    ", bearing=" + bearing +
                    '}';
        }

        public MockGPSItem setLng(Double lng) {
            this.lng = lng;
            return this;
        }

        public MockGPSItem setLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public MockGPSItem setTime(Long time) {
            this.time = time;
            return this;
        }

        public MockGPSItem setAccuracy(Float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public MockGPSItem setSpeed(Double speed) {
            this.speed = speed;
            return this;
        }

        public MockGPSItem setBearing(Long bearing) {
            this.bearing = bearing;
            return this;
        }
    }


    public static void modifyBearing(ArrayList<MockGPSItem> mockGPSItems) {
        int compairStep = 5;
        if (mockGPSItems != null && mockGPSItems.size() > compairStep) {
//            LogUtils.d(TAG, "⚠️modifyBearing() called with: mockGPSItems = [" + mockGPSItems.size() + "]");
            for (int i = 0; i < mockGPSItems.size() - compairStep; i++) {
                MockGPSItem a = mockGPSItems.get(i);
                MockGPSItem b = mockGPSItems.get(i + compairStep);
//                LogUtils.d(TAG, "⚠️modifyBearing() called with:" +
//                        "bearing = [" + a.bearing + "] " +
//                        "bearing = [" + (long) BearingUtils.bearing(a.lat, a.lng, b.lat, b.lng) + "]");
                a.bearing = (long) BearingUtils.bearing(a.lat, a.lng, b.lat, b.lng);
            }
        }
    }
}

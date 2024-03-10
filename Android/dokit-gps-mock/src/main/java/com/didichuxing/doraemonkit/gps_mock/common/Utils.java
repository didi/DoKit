package com.didichuxing.doraemonkit.gps_mock.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Utils {
    private static final double PI = 3.14159265;

    public static ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = value;
            v.setLayoutParams(layoutParams);
        });
        return animator;
    }

    public static Animator createAlphaAnimator(View view, float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", start, end);
        return animator;
    }

    public static Animator createRotateAnimator(View view, float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", start, end);
        animator.setDuration(300);
        return animator;
    }


    /**
     * 功能：通过反射 设置指定类对象中的 指定属性的值
     *
     * @param obj           类对象
     * @param propertyName  要设置的属性名
     * @param propertyvalue 要设置的属性的值
     */
    public static void set(Object obj, String propertyName, Object propertyvalue) {
        try {
            // step1 获取属性指针
            Field declaredField = obj.getClass().getDeclaredField(propertyName);
            // step2 设置属性可访问
            declaredField.setAccessible(true);
            // step3 设置属性的值
            declaredField.set(obj, propertyvalue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param radius 单位米
     * @return minLat, minLng, maxLat, maxLng
     */
    public static double[] getAround(double centerLat, double centerLng, double radius) {
        double degree = (24901 * 1609) / 360.0;

        double dpmLat = 1 / degree;
        double radiusLat = dpmLat * radius;
        double minLat = centerLat - radiusLat;
        double maxLat = centerLat + radiusLat;

        double mpdLng = degree * Math.cos(centerLat * (PI / 180));
        double dpmLng = 1 / mpdLng;
        double radiusLng = dpmLng * radius;
        double minLng = centerLng - radiusLng;
        double maxLng = centerLng + radiusLng;
        Log.d("range_lat_lng", "[" + minLat + "," + minLng + "," + maxLat + "," + maxLng + "]");
        return new double[]{minLat, minLng, maxLat, maxLng};
    }

    public static double getDistance(LatLng startLagLng, LatLng desLagLng) {
        double distance = DistanceUtil.getDistance(startLagLng, desLagLng);
        Log.d("distance_between", "startLagLng=" + startLagLng.toString() + " desLagLng=" + desLagLng.toString() + " distance=" + distance);
        return distance;
    }

    /**
     * 获取一段路径的总路程.
     */
    public static double getRouteDistance(List<LatLng> points) {
        if (points == null || points.size() <= 0) return 0;
        double distance = 0;
        for (int i = 0; i + 1 < points.size(); i++) {
            distance += getDistance(points.get(i), points.get(i + 1));
        }
        return distance;
    }

    /**
     * 获取随机坐标点:根据给定的圆心,半径和随机生成的目标纬度坐标, 获取随机的经度坐标
     */
    public static double[] getRandomLatLng(double latitude, double longitude, double radius, double[] rangeAround) {
        // 获取随机纬度坐标
        double randomLat = rangeAround[0] + (rangeAround[2] - rangeAround[0]) * Math.random();

        // 直角三角形一条直角边长度
        double y = getDistance(new LatLng(randomLat, longitude), new LatLng(latitude, longitude));
        // 计算直接三角形另一条直角边长度
        double x = Math.sqrt(Math.pow(radius, 2) - Math.pow(y, 2));
        // 根据x长度换算出对应经度值
        double differLng = (x / radius) * ((rangeAround[3] - rangeAround[1]) / 2);
        Random random = new Random();
        // 生成对应的随机经度坐标
        double randomLng = longitude + (random.nextBoolean() ? differLng : -differLng);

        return new double[]{randomLat, randomLng};
    }

    /**
     * 获取纬度漂移偏移量
     */
    public static double getOrientLatDiffer(double[] rangeAround) {
        return ((rangeAround[2] - rangeAround[0]) / 2) * Math.random();
    }

    /**
     * 获取定向坐标点(给路径漂移制造漂移点)
     */
    public static double[] getOrientationLatLng(double centerLat, double centerLng, double radius,  double[] rangeAround, double latDiffer) {
        double destRandomLat = centerLat + latDiffer;

        // 直角三角形一条直角边长度
        double y = getDistance(new LatLng(destRandomLat, centerLng), new LatLng(centerLat, centerLng));
        // 计算直接三角形另一条直角边长度
        double x = Math.sqrt(Math.pow(radius, 2) - Math.pow(y, 2));
        // 根据x长度换算出对应经度值
        double differLng = (x / radius) * ((rangeAround[3] - rangeAround[1]) / 2);
        // 生成对应的随机经度坐标(默认中心点坐标 + 偏移值)
        double randomLng = centerLng + differLng;

        return new double[]{destRandomLat, randomLng};
    }

    public static void randomSet(int min, int max, int n, HashSet<Integer> set) {
        if (n > (max - min + 1) || max < min) {
            return;
        }
        for (int i = 0; i < n; i++) {
            // 调用Math.random()方法
            int num = (int) (Math.random() * (max - min)) + min;
            set.add(num);// 将不同的数存入HashSet中
        }
        int setSize = set.size();
        // 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
        if (setSize < n) {
            randomSet(min, max, n - setSize, set);// 递归
        }
    }

}

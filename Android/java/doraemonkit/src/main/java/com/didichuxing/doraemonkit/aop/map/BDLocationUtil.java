//package com.didichuxing.doraemonkit.aop;
//
//import com.baidu.location.BDLocation;
//import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
//
///**
// * ================================================
// * 作    者：jint（金台）
// * 版    本：1.0
// * 创建日期：2019-12-18-20:01
// * 描    述：对百度地图返回的位置信息进行hook
// * 修订历史：
// * ================================================
// */
//public class BDLocationUtil {
//    public static BDLocation proxy(BDLocation bdLocation) {
//        if (GpsMockManager.getInstance().isMocking()) {
//            try {
//                bdLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
//                bdLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return bdLocation;
//    }
//}

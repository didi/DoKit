package com.didichuxing.doraemonkit.kit.connect.data

/**
 *   "deviceName": "小米测试手机-张三",//设备名称(可通过前端修改)
 *   "deviceType": "android/ios",//字母全小写
 *   "osVersion": "11"，//系统版本
 *   "screen": "1080*1960"，//屏幕分辨率
 *   "manufacturer": "huawei"，//厂商信息
 *   "ip": "1080*1960"，//ip地址
 *   "connectSerial": "widszdwewqe7qwer"，//随机连接序列号16位(没有则发送空)
 *   "appName": "滴滴代驾司机端"，//应用名称
 *   "appVersion": "3.6.0"，//应用版本
 */
data class LoginData(

    val deviceName: String,
    val deviceType: String,
    val osVersion: String,
    val screen: String,
    val manufacturer: String,
    val ip: String,
    val connectSerial: String = "",
    val appName: String,
    val appVersion: String
)

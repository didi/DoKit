package com.didichuxing.doraemonkit.util.netstate;

/**
 * @author maple on 2019/7/9 15:10.
 * @version v1.0
 * @see 1040441325@qq.com
 * 不太好的设计,该枚举有双重含义
 * Inetwork实现类中使用{WIFI,CMNET,CMWAP,NONE} 表示网络状态
 * NetWork注解中{AUTO,WIFI,CMNET,CMWAP} 表示方法需要监听的网络类型
 */
public enum NetType {
    AUTO,//有网,不关心网络类型
    WIFI,
    CMNET,//PC,笔记本,PAD
    CMWAP,//手机网络
    NONE
}

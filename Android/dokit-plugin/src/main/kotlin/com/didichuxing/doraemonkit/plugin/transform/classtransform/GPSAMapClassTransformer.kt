package com.didichuxing.doraemonkit.plugin.transform.classtransform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.lastPath
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.className
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 3:06 下午
 * @Description 高德地图 hooks
 */

class GPSAMapClassTransformer : AbsClassTransformer() {

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        if (dokit.gpsEnable && dokit.gps.amap && DoKitExtUtil.DOKIT_GPS_MOCK_INCLUDE) {
            //插入高德地图定位相关字节码
            if (className == "com.amap.api.location.AMapLocationClient") {
                //设置监听器
                klass.methods?.find {
                    it.name == "setLocationListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapLocationInsnList())
                }
                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(
                        createAmapLocationUnRegisterInsnList()
                    )
                }
                //代理getLastKnownLocation
                klass.methods?.find {
                    it.name == "getLastKnownLocation"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook AMapLocationClient getLastKnownLocation  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAMapClientLastKnownLocation())
                }

            }
//            //插入高德 地图定位相关字节码
//            if (className == "com.amap.api.maps.AMap") {
//                //设置LocationSource代理
//                klass.methods?.find {
//                    it.name == "setLocationSource"
//                }.let { methodNode ->
//                    "${context.projectDir.lastPath()}->hook amap map LocationSource  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
//                    methodNode?.instructions?.insert(createAmapLocationSourceInsnList())
//                }
//            }

            //插入高德地图导航相关字节码
            if (className == "com.amap.api.navi.AMapNavi") {
                //设置监听器
                klass.methods?.find {
                    it.name == "addAMapNaviListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapNaviInsnList())
                }
                //反注册监听器
                klass.methods?.find {
                    it.name == "removeAMapNaviListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(
                        createAmapNaviUnRegisterInsnList()
                    )
                }
            }
        }
        return klass
    }

    /**
     * 创建Amap地图代码指令
     */
    private fun createAmapLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的 setLocationListener 方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapLocationListenerProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapLocationListenerProxy",
                    "<init>",
                    "(Lcom/amap/api/location/AMapLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }

    /**
     * 创建Amap地图导航代码指令
     */
    private fun createAmapNaviInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapNavi的addAMapNaviListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapNaviListenerProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapNaviListenerProxy",
                    "<init>",
                    "(Lcom/amap/api/navi/AMapNaviListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }


    /**
     * 创建Amap LocationSource代码指令
     */
    private fun createAmapLocationSourceInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapNavi的addAMapNaviListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapLocationSourceProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapLocationSourceProxy",
                    "<init>",
                    "(Lcom/amap/api/maps/LocationSource;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }

    /**
     * 创建AMapLocationClient#LastKnownLocation 字节码替换
     */
    private fun createAMapClientLastKnownLocation(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/AMapLocationClientProxy",
                    "getLastKnownLocation",
                    "(Lcom/amap/api/location/AMapLocationClient;)Lcom/amap/api/location/AMapLocation;",
                    false
                )
            )
            add(InsnNode(Opcodes.ARETURN))
            this
        }

    }

    /**
     * 创建Amap地图UnRegister代码指令
     */
    private fun createAmapLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/ThirdMapLocationListenerUtil",
                    "unRegisterAmapLocationListener",
                    "(Lcom/amap/api/location/AMapLocationListener;)Lcom/didichuxing/doraemonkit/gps_mock/map/AMapLocationListenerProxy;",
                    false
                )
            )
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }
    }

    /**
     * 创建Amap地图 Navi UnRegister代码指令
     */
    private fun createAmapNaviUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/ThirdMapLocationListenerUtil",
                    "unRegisterAmapNaviListener",
                    "(Lcom/amap/api/navi/AMapNaviListener;)Lcom/didichuxing/doraemonkit/gps_mock/map/AMapNaviListenerProxy;",
                    false
                )
            )
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }

}

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
 * @Description 百度地图 hooks
 */

class GPSBDClassTransformer : AbsClassTransformer() {

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        if (dokit.gpsEnable && dokit.gps.baidu && DoKitExtUtil.DOKIT_GPS_MOCK_INCLUDE) {
            //插入百度地图相关字节码
            if (className == "com.baidu.location.LocationClient") {
                //拦截注册监听器
                klass.methods?.filter {
                    it.name == "registerLocationListener"
                        && (it.desc == "(Lcom/baidu/location/BDLocationListener;)V" || it.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V")
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    if (methodNode.desc == "(Lcom/baidu/location/BDLocationListener;)V") {
                        methodNode?.instructions?.insert(createBDLocationListenerInsnList())
                    } else if (methodNode.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V") {
                        methodNode?.instructions?.insert(createBDLocationAbsListenerInsnList())
                    }
                }
                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener" && it.desc == "(Lcom/baidu/location/BDLocationListener;)V"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(
                        createBDLocationUnRegisterInsnList()
                    )
                }
                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener" && it.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(
                        createBDAbsLocationUnRegisterInsnList()
                    )
                }
            }
        }
        return klass
    }

    /**
     * 创建百度地图代码指令
     */
    private fun createBDLocationListenerInsnList(): InsnList {
        return with(InsnList()) {
            //在LocationClient的registerLocationListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/BDLocationListenerProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/BDLocationListenerProxy",
                    "<init>",
                    "(Lcom/baidu/location/BDLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))

            this
        }

    }

    /**
     * 创建百度地图代码指令
     */
    private fun createBDLocationAbsListenerInsnList(): InsnList {
        return with(InsnList()) {
            //在LocationClient的registerLocationListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/BDAbsLocationListenerProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/BDAbsLocationListenerProxy",
                    "<init>",
                    "(Lcom/baidu/location/BDAbstractLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }
    }

    /**
     * 创建百度地图UnRegister代码指令
     */
    private fun createBDLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/ThirdMapLocationListenerUtil",
                    "unRegisterBDLocationListener",
                    "(Lcom/baidu/location/BDLocationListener;)Lcom/didichuxing/doraemonkit/gps_mock/map/BDLocationListenerProxy;",
                    false
                )
            )
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }

    /**
     * 创建百度地图UnRegister代码指令
     */
    private fun createBDAbsLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/ThirdMapLocationListenerUtil",
                    "unRegisterBDLocationListener",
                    "(Lcom/baidu/location/BDAbstractLocationListener;)Lcom/didichuxing/doraemonkit/gps_mock/map/BDAbsLocationListenerProxy;",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }

    /**
     * 创建百度地图代码指令
     */
    private fun createBaiduLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/BDLocationUtil",
                    "proxy",
                    "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }
}

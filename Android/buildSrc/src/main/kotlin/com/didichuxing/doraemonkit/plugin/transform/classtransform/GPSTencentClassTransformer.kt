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
 * @Description 腾讯地图 hooks
 */

class GPSTencentClassTransformer : AbsClassTransformer() {

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        if (dokit.gpsEnable && dokit.gps.tencent && DoKitExtUtil.DOKIT_GPS_MOCK_INCLUDE) {
            //插入腾讯地图相关字节码
            if (className == "com.tencent.map.geolocation.TencentLocationManager") {
                //持续定位和单次定位
                klass.methods?.filter {
                    it.name == "requestSingleFreshLocation" || it.name == "requestLocationUpdates"
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createTencentLocationInsnList())
                }

                //反注册监听器
                klass.methods?.find {
                    it.name == "removeUpdates"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(
                        createTencentLocationUnRegisterInsnList()
                    )
                }
            }
        }
        return klass
    }

    /**
     * 创建tencent地图代码指令
     */
    private fun createTencentLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    Opcodes.NEW,
                    "com/didichuxing/doraemonkit/gps_mock/map/TencentLocationListenerProxy"
                )
            )
            add(InsnNode(Opcodes.DUP))
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 2))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/gps_mock/map/TencentLocationListenerProxy",
                    "<init>",
                    "(Lcom/tencent/map/geolocation/TencentLocationListener;)V",
                    false
                )
            )
            //对第二个参数进行重新赋值
            add(VarInsnNode(Opcodes.ASTORE, 2))

            this
        }

    }

    /**
     * 创建Tencent地图UnRegister代码指令
     */
    private fun createTencentLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/gps_mock/map/ThirdMapLocationListenerUtil",
                    "unRegisterTencentLocationListener",
                    "(Lcom/tencent/map/geolocation/TencentLocationListener;)Lcom/didichuxing/doraemonkit/gps_mock/map/TencentLocationListenerProxy;",
                    false
                )
            )
            add(VarInsnNode(Opcodes.ASTORE, 1))
            this
        }

    }


}

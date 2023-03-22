package com.didichuxing.doraemonkit.plugin.transform.classtransform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.lastPath
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.className
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 3:06 下午
 * @Description 系统定位hooks
 */

class GPSClassTransformer : AbsClassTransformer() {

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        //gps字节码操作
        if (dokit.gpsEnable && dokit.gps.local && DoKitExtUtil.DOKIT_GPS_MOCK_INCLUDE) {
            //系统 gpsStatus hook
            klass.methods.forEach { method ->
                method.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                        it.opcode == Opcodes.INVOKEVIRTUAL &&
                            it.owner == "android/location/LocationManager" &&
                            it.name == "getGpsStatus" &&
                            it.desc == "(Landroid/location/GpsStatus;)Landroid/location/GpsStatus;"
                    }?.forEach {
                        "${context.projectDir.lastPath()}->hook LocationManager#getGpsStatus method  succeed in : ${className}_${method.name}_${method.desc}".println()
                        method.instructions.insert(
                            it,
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "com/didichuxing/doraemonkit/gps_mock/location/GpsStatusUtil",
                                "wrap",
                                "(Landroid/location/GpsStatus;)Landroid/location/GpsStatus;",
                                false
                            )
                        )
                    }
            }
        }
        return klass
    }


}

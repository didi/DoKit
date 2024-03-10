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
 * @Description 用一句话说明文件功能
 */

class ThirdLibsClassTransformer : AbsClassTransformer() {

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        if (dokit.thirdLibEnable) {
            //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
            if (className == "com.didichuxing.doraemonkit.DoKitReal") {
                //三方库信息注入
                klass.methods?.find {
                    it.name == "initThirdLibraryInfo"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->insert map to the DoKitReal initThirdLibraryInfo succeed".println()
                    methodNode?.instructions?.insert(createThirdLibInfoInsnList())
                }
            }
        }

        return klass
    }

    /**
     * 创建pluginConfig代码指令
     */
    private fun createThirdLibInfoInsnList(): InsnList {
        //val insnList = InsnList()
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(Opcodes.NEW, "java/util/HashMap"))
            add(InsnNode(Opcodes.DUP))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(Opcodes.ASTORE, 0))
            DoKitExtUtil.THIRD_LIB_INFOS.forEach { thirdLibInfo ->
                add(VarInsnNode(Opcodes.ALOAD, 0))
                add(LdcInsnNode(thirdLibInfo.variant))
                add(LdcInsnNode(thirdLibInfo.fileSize.toString()))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKEINTERFACE,
                        "java/util/Map",
                        "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                        false
                    )
                )
                add(InsnNode(Opcodes.POP))
            }
            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitThirdLibInfo",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )
            this
        }
    }


}

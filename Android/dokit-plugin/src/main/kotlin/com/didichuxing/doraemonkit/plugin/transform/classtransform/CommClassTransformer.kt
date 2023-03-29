package com.didichuxing.doraemonkit.plugin.transform.classtransform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExtension
import com.didichuxing.doraemonkit.plugin.formatSuperName
import com.didichuxing.doraemonkit.plugin.lastPath
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.className
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
class CommClassTransformer : AbsClassTransformer() {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        val className = klass.className
        val superName = klass.formatSuperName

        //hook Androidx的ComponentActivity
        if (className != "com.didichuxing.doraemonkit.aop.mc.DoKitProxyActivity" && superName == "android.app.Activity") {
            createComponentActivitySuperActivityImpl(klass)
        }

        return super.transform(context, klass)
    }

    /**
     * 类处理转化实现
     */
    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoKitReal") {
            //插件配置
            klass.methods?.find {
                it.name == "pluginConfig"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoKitReal pluginConfig succeed".println()
                methodNode?.instructions?.insert(createPluginConfigInsnList(dokit.gpsEnable, dokit.networkEnable, dokit.bigImageEnable))
            }
        }
        return klass
    }

    /**
     * 创建pluginConfig代码指令
     */
    private fun createPluginConfigInsnList(gpsSwitch: Boolean, networkSwitch: Boolean, bigImgSwitch: Boolean): InsnList {
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(Opcodes.NEW, "java/util/HashMap"))
            add(InsnNode(Opcodes.DUP))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(Opcodes.ASTORE, 0))
            //获取第一个变量
            //put("dokitPluginSwitch",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("dokitPluginSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitPluginSwitchOpen()) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("gpsSwitch",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("gpsSwitch"))
            add(InsnNode(if (gpsSwitch) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("networkSwitch",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("networkSwitch"))
            add(InsnNode(if (networkSwitch) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("bigImgSwitch"))
            add(InsnNode(if (bigImgSwitch) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("methodSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitSlowMethodSwitchOpen()) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))


            //put("methodStrategy",0)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("methodStrategy"))
            add(InsnNode(if (DoKitExtUtil.SLOW_METHOD_STRATEGY == SlowMethodExtension.STRATEGY_STACK) Opcodes.ICONST_0 else Opcodes.ICONST_1))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Integer",
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitPluginConfig",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )
            this
        }
    }

    /**
     * 重置ComponentActivity的父类
     */
    private fun createComponentActivitySuperActivityImpl(klass: ClassNode) {
        /**
         * 修改继承的父类
         */
        klass.superName = "com/didichuxing/doraemonkit/aop/mc/DoKitProxyActivity"
    }

}

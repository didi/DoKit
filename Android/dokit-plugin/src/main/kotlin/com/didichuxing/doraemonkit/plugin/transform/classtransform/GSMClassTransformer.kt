package com.didichuxing.doraemonkit.plugin.transform.classtransform

import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExtension
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.asIterable
import com.didiglobal.booster.transform.asm.className
import org.gradle.api.Project
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：
 *           全局慢函数业务代码慢函数
 *           wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 *
 * 修订历史：
 * ================================================
 *
 *  全局慢函数
 *
 */
class GSMClassTransformer : AbsClassTransformer() {
    val thresholdTime = DoKitExtUtil.slowMethodExt.normalMethod.thresholdTime

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {

        if (!DoKitExtUtil.dokitSlowMethodSwitchOpen()) {
            return klass
        }

        if (DoKitExtUtil.SLOW_METHOD_STRATEGY == SlowMethodExtension.STRATEGY_STACK) {
            return klass
        }

        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
            return klass
        }


        val className = klass.className
        //没有自定义设置插装包名 默认是以packageName为包名 即全局业务代码插桩
        DoKitExtUtil.slowMethodExt.normalMethod.packageNames.forEach { packageName ->
            //包含在白名单中且不在黑名单中
            if (className.contains(packageName) && notMatchedBlackList(className)) {
                klass.methods.filter { methodNode ->
                    methodNode.name != "<init>" &&
                        !methodNode.isEmptyMethod() &&
                        !methodNode.isSingleMethod() &&
                        !methodNode.isGetSetMethod() &&
                        !methodNode.isMainMethod(className)
                }.forEach { methodNode ->
                    methodNode.instructions.asIterable()
                        .filterIsInstance(MethodInsnNode::class.java).let { methodInsnNodes ->
                            if (methodInsnNodes.isNotEmpty()) {
                                //方法入口插入
                                methodNode.instructions.insert(
                                    createMethodEnterInsnList(
                                        className,
                                        methodNode.name,
                                        methodNode.access
                                    )
                                )
                                //方法出口插入
                                methodNode.instructions.getMethodExitInsnNodes()
                                    ?.forEach { methodExitInsnNode ->
                                        methodNode.instructions.insertBefore(
                                            methodExitInsnNode,
                                            createMethodExitInsnList(
                                                className,
                                                methodNode.name,
                                                methodNode.access
                                            )
                                        )
                                    }
                            }
                        }
                }
            }
        }
        return klass
    }


    private fun notMatchedBlackList(className: String): Boolean {
        for (strBlack in DoKitExtUtil.slowMethodExt.normalMethod.methodBlacklist) {
            if (className.contains(strBlack)) {
                return false
            }
        }

        return true
    }

    /**
     * 创建慢函数入口指令集
     */
    private fun createMethodEnterInsnList(
        className: String,
        methodName: String,
        access: Int
    ): InsnList {
        val isStaticMethod = access and Opcodes.ACC_STATIC != 0
        return with(InsnList()) {
            if (isStaticMethod) {
                add(
                    FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "INSTANCE",
                        "Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;"
                    )
                )
                add(IntInsnNode(Opcodes.SIPUSH, thresholdTime))
                add(LdcInsnNode("$className&$methodName"))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "recodeStaticMethodCostStart",
                        "(ILjava/lang/String;)V",
                        false
                    )
                )
            } else {
                add(
                    FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "INSTANCE",
                        "Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;"
                    )
                )
                add(IntInsnNode(Opcodes.SIPUSH, thresholdTime))
                add(LdcInsnNode("$className&$methodName"))
                add(VarInsnNode(Opcodes.ALOAD, 0))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "recodeObjectMethodCostStart",
                        "(ILjava/lang/String;Ljava/lang/Object;)V",
                        false
                    )
                )
            }

            this
        }

    }


    /**
     * 创建慢函数退出时的指令集
     */
    private fun createMethodExitInsnList(
        className: String,
        methodName: String,
        access: Int
    ): InsnList {
        val isStaticMethod = access and Opcodes.ACC_STATIC != 0
        return with(InsnList()) {
            if (isStaticMethod) {
                add(
                    FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "INSTANCE",
                        "Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;"
                    )
                )
                add(IntInsnNode(Opcodes.SIPUSH, thresholdTime))
                add(LdcInsnNode("$className&$methodName"))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "recodeStaticMethodCostEnd",
                        "(ILjava/lang/String;)V",
                        false
                    )
                )
            } else {
                add(
                    FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "INSTANCE",
                        "Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;"
                    )
                )
                add(IntInsnNode(Opcodes.SIPUSH, thresholdTime))
                add(LdcInsnNode("$className&$methodName"))
                add(VarInsnNode(Opcodes.ALOAD, 0))
                add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        "com/didichuxing/doraemonkit/aop/MethodCostUtil",
                        "recodeObjectMethodCostEnd",
                        "(ILjava/lang/String;Ljava/lang/Object;)V",
                        false
                    )
                )
            }
            this
        }
    }


}

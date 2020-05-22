package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt
import com.didichuxing.doraemonkit.plugin.getMethodExitInsnNodes
import com.didichuxing.doraemonkit.plugin.ownerClassName
import com.didichuxing.doraemonkit.plugin.stack_method.MethodStackNode
import com.didichuxing.doraemonkit.plugin.stack_method.MethodStackNodeUtil
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.asIterable
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：入口函数 慢函数调用栈 wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：不要指定自动注入 需要手动在DoKitAsmTransformer中通过配置创建
 * 原理:transform()方法的调用是无序的  原因:哪一个class会先被transformer执行是不确定的  但是每一个class被transformer执行顺序是遵循transformer的Priority规则的
 * ================================================
 */
@Priority(4)
@AutoService(ClassTransformer::class)
class EnterMethodStackTransformer : ClassTransformer {

    private val thresholdTime = DoKitExtUtil.slowMethodExt.stackMethod.thresholdTime
    private val level = 0
    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        if (!DoKitExtUtil.slowMethodExt.methodSwitch) {
            return klass
        }

        if (DoKitExtUtil.slowMethodExt.strategy == SlowMethodExt.STRATEGY_NORMAL) {
            return klass
        }

        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
            return klass
        }

        //默认为Application onCreate 和attachBaseContext
        val enterMethods = DoKitExtUtil.slowMethodExt.stackMethod.enterMethods
        //找不到配置的Application
        if (enterMethods.isEmpty()) {
            val superName = klass.superName
            //先判断父类
            if (superName.isNotEmpty() && (superName == "android/app/Application" ||
                            superName == "android/support/multidex/MultiDexApplication" ||
                            superName == "androidx/multidex/MultiDexApplication")) {
                klass.methods.filter { methodNode ->
                    (methodNode.name == "onCreate" && methodNode.desc == "()V") || (methodNode.name == "attachBaseContext" && methodNode.desc == "(Landroid/content/Context;)V")
                }.let { methodNodes ->
                    //读取全是函数调用的Insn
                    methodNodes.forEach { methodNode ->
                        operateMethodInsn(klass, methodNode)
                    }
                }
            }
        } else {
            enterMethods.forEach { enterMethodName ->
                klass.methods.forEach { methodNode ->
                    val allMethodName = "${klass.className}.${methodNode.name}"
                    if (allMethodName == enterMethodName) {
                        println("level===>$level   mathched enterMethod===>$allMethodName")
                        operateMethodInsn(klass, methodNode)
                    }
                }
            }
        }

        return klass
    }


    private fun operateMethodInsn(klass: ClassNode, methodNode: MethodNode) {
        //读取全是函数调用的指令
        methodNode.instructions.asIterable().filterIsInstance(MethodInsnNode::class.java).filter { methodInsnNode ->
            methodInsnNode.name != "<init>"
        }.forEach { methodInsnNode ->
            val methodStackNode = MethodStackNode(level, methodInsnNode.ownerClassName, methodInsnNode.name, methodInsnNode.desc, klass.className, methodNode.name, methodNode.desc)
            MethodStackNodeUtil.addMethodStackNode(level, methodStackNode)
        }
        //函数出入口插入耗时统计代码
        //方法入口插入
        methodNode.instructions.insert(createMethodEnterInsnList(level, klass.className, methodNode.name, methodNode.desc, methodNode.access))
        //方法出口插入
        methodNode.instructions.getMethodExitInsnNodes()?.forEach { methodExitInsnNode ->
            methodNode.instructions.insertBefore(methodExitInsnNode, createMethodExitInsnList(level, klass.className, methodNode.name, methodNode.desc, methodNode.access))
        }
    }


    /**
     * 创建慢函数入口指令集
     */
    private fun createMethodEnterInsnList(level: Int, className: String, methodName: String, desc: String, access: Int): InsnList {
        val isStaticMethod = access and ACC_STATIC != 0
        val insnList = InsnList()
        if (isStaticMethod) {
            insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false))
            insnList.add(IntInsnNode(BIPUSH, DoKitExtUtil.mStackMethodLevel))
            insnList.add(IntInsnNode(BIPUSH, thresholdTime))
            insnList.add(IntInsnNode(BIPUSH, level))
            insnList.add(LdcInsnNode(className))
            insnList.add(LdcInsnNode(methodName))
            insnList.add(LdcInsnNode(desc))
            insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeStaticMethodCostStart", "(IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false))
        } else {
            insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false))
            insnList.add(IntInsnNode(BIPUSH, DoKitExtUtil.mStackMethodLevel))
            insnList.add(IntInsnNode(BIPUSH, thresholdTime))
            insnList.add(IntInsnNode(BIPUSH, level))
            insnList.add(LdcInsnNode(className))
            insnList.add(LdcInsnNode(methodName))
            insnList.add(LdcInsnNode(desc))
            insnList.add(VarInsnNode(ALOAD, 0))
            insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeObjectMethodCostStart", "(IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false))
        }

        return insnList
    }


    /**
     * 创建慢函数退出时的指令集
     */
    private fun createMethodExitInsnList(level: Int, className: String, methodName: String, desc: String, access: Int): InsnList {
        val isStaticMethod = access and ACC_STATIC != 0
        val insnList = InsnList()
        if (isStaticMethod) {
            insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false))
            insnList.add(IntInsnNode(BIPUSH, thresholdTime))
            insnList.add(IntInsnNode(BIPUSH, level))
            insnList.add(LdcInsnNode(className))
            insnList.add(LdcInsnNode(methodName))
            insnList.add(LdcInsnNode(desc))
            insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeStaticMethodCostEnd", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false))
        } else {
            insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false))
            insnList.add(IntInsnNode(BIPUSH, thresholdTime))
            insnList.add(IntInsnNode(BIPUSH, level))
            insnList.add(LdcInsnNode(className))
            insnList.add(LdcInsnNode(methodName))
            insnList.add(LdcInsnNode(desc))
            insnList.add(VarInsnNode(ALOAD, 0))
            insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeObjectMethodCostEnd", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false))
        }
        return insnList
    }

}


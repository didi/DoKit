package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
@Priority(1)
@AutoService(ClassTransformer::class)
class UrlConnectionTransformer : ClassTransformer {
    private val SHADOW_URL = "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"
    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
//        if(klass.className == "com.didichuxing.doraemondemo.App"){
//            println("===UrlConnectionTransformer====transform===")
//        }
        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        if (!DoKitExtUtil.commExt.networkSwitch) {
            return klass
        }

        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
            return klass
        }

        klass.methods.forEach { method ->
            method.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                it.opcode == INVOKEVIRTUAL &&
                        it.owner == "java/net/URL" &&
                        it.name == "openConnection" &&
                        it.desc == "()Ljava/net/URLConnection;"
            }?.forEach {
                method.instructions.insert(it, MethodInsnNode(INVOKESTATIC, SHADOW_URL, "proxy", DESC, false))
            }
        }

        return klass
    }


}


package com.didichuxing.doraemonkit.plugin.transform.classtransform

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
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
class UrlConnectionTransformer : AbsClassTransformer() {

    private val SHADOW_URL = "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"

    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"

    override fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className

        if (dokit.networkEnable && dokit.network.urlConnect) {
            // url connection
            klass.methods.forEach { method ->
                method.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                        it.opcode == Opcodes.INVOKEVIRTUAL &&
                            it.owner == "java/net/URL" &&
                            it.name == "openConnection" &&
                            it.desc == "()Ljava/net/URLConnection;"
                    }?.forEach {
                        "${context.projectDir.lastPath()}->hook URL#openConnection method  succeed in : ${className}_${method.name}_${method.desc}".println()
                        method.instructions.insert(
                            it,
                            MethodInsnNode(Opcodes.INVOKESTATIC, SHADOW_URL, "proxy", DESC, false)
                        )
                    }
            }
        }
        return klass
    }


}

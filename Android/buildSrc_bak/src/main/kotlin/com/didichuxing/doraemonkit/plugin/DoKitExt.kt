package com.didichuxing.doraemonkit.plugin

import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.kotlinx.boolean
import com.didiglobal.booster.transform.TransformContext
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/19-18:00
 * 描    述：dokit 对象扩展
 * 修订历史：
 * ================================================
 */

fun InsnList.getMethodExitInsnNodes(): Sequence<InsnNode>? {
    return this.iterator()?.asSequence()?.filterIsInstance(InsnNode::class.java)?.filter {
        it.opcode == RETURN ||
                it.opcode == IRETURN ||
                it.opcode == FRETURN ||
                it.opcode == ARETURN ||
                it.opcode == LRETURN ||
                it.opcode == DRETURN ||
                it.opcode == ATHROW
    }
}

fun BaseVariant.isRelease(): Boolean {
    if (this.name.contains("release") || this.name.contains("Release")) {
        return true
    }
    return false
}


fun TransformContext.isRelease(): Boolean {
    if (this.name.contains("release") || this.name.contains("Release")) {
        return true
    }
    return false
}


fun String.println() {
    if (DoKitExtUtil.dokitLogSwitchOpen()) {
        print(this)
    }
}

val MethodInsnNode.ownerClassName: String
    get() = owner.replace('/', '.')
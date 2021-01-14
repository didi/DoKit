package com.didichuxing.doraemonkit.plugin

import com.android.build.gradle.api.BaseVariant
import com.didiglobal.booster.transform.TransformContext
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/19-18:00
 * 描    述：dokit 对象扩展
 * 修订历史：
 * ================================================
 */

fun MethodNode.isGetSetMethod(): Boolean {
    var ignoreCount = 0
    val iterator = instructions.iterator()
    while (iterator.hasNext()) {
        val insnNode = iterator.next()
        val opcode = insnNode.opcode
        if (-1 == opcode) {
            continue
        }
        if (opcode != GETFIELD && opcode != GETSTATIC && opcode != H_GETFIELD && opcode != H_GETSTATIC && opcode != RETURN && opcode != ARETURN && opcode != DRETURN && opcode != FRETURN && opcode != LRETURN && opcode != IRETURN && opcode != PUTFIELD && opcode != PUTSTATIC && opcode != H_PUTFIELD && opcode != H_PUTSTATIC && opcode > SALOAD) {
            if (name.equals("<init>") && opcode == INVOKESPECIAL) {
                ignoreCount++
                if (ignoreCount > 1) {
                    return false
                }
                continue
            }
            return false
        }
    }
    return true
}

fun MethodNode.isSingleMethod(): Boolean {
    val iterator = instructions.iterator()
    while (iterator.hasNext()) {
        val insnNode = iterator.next()
        val opcode = insnNode.opcode
        if (-1 == opcode) {
            continue
        } else if (INVOKEVIRTUAL <= opcode && opcode <= INVOKEDYNAMIC) {
            return false
        }
    }
    return true
}

fun MethodNode.isEmptyMethod(): Boolean {
    val iterator = instructions.iterator()
    while (iterator.hasNext()) {
        val insnNode = iterator.next()
        val opcode = insnNode.opcode
        return if (-1 == opcode) {
            continue
        } else {
            false
        }
    }
    return true
}


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
        println("[dokit plugin]===>$this")
    }
}

fun File.lastPath(): String {
    return this.path.split("/").last()

}

val MethodInsnNode.ownerClassName: String
    get() = owner.replace('/', '.')
package com.didichuxing.doraemonkit.plugin

import com.android.build.gradle.api.BaseVariant
import com.android.dex.DexFormat
import com.android.dx.command.dexer.Main
import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.kotlinx.redirect
import com.didiglobal.booster.kotlinx.search
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.util.transform
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.*
import java.util.jar.JarFile
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

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

fun MethodNode.isMainMethod(className: String): Boolean {
    if (this.name == "main" && this.desc == "([Ljava/lang/String;)V") {
        "====isMainMethod====$className  ${this.name}   ${this.desc}   ${this.access}".println()
        return true
    }

    return false
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


val ClassNode.formatSuperName: String
    get() = superName.replace('/', '.')

internal fun File.dex(output: File, api: Int = DexFormat.API_NO_EXTENDED_OPCODES): Int {
    val args = Main.Arguments().apply {
        numThreads = NCPU
        debug = true
        warnings = true
        emptyOk = true
        multiDex = true
        jarOutput = true
        optimize = false
        minSdkVersion = api
        fileNames = arrayOf(output.canonicalPath)
        outName = canonicalPath
    }
    return try {
        Main.run(args)
    } catch (t: Throwable) {
        t.printStackTrace()
        -1
    }
}

/**
 * Transform this file or directory to the output by the specified transformer
 *
 * @param output The output location
 * @param transformer The byte data transformer
 */
fun File.dokitTransform(output: File, transformer: (ByteArray) -> ByteArray = { it -> it }) {
    when {
        isDirectory -> this.toURI().let { base ->
            this.search().parallelStream().forEach {
                it.transform(File(output, base.relativize(it.toURI()).path), transformer)
            }
        }
        isFile -> when (extension.toLowerCase()) {
            "jar" -> JarFile(this).use {
                it.dokitTransform(output, ::JarArchiveEntry, transformer)
            }
            "class" -> this.inputStream().use {
                it.transform(transformer).redirect(output)
            }
            else -> this.copyTo(output, true)
        }
        else -> throw IOException("Unexpected file: ${this.canonicalPath}")
    }
}

fun ZipFile.dokitTransform(
    output: File,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) = output.touch().outputStream().buffered().use {
    this.dokitTransform(it, entryFactory, transformer)
}


fun ZipFile.dokitTransform(
    output: OutputStream,
    entryFactory: (ZipEntry) -> ZipArchiveEntry = ::ZipArchiveEntry,
    transformer: (ByteArray) -> ByteArray = { it -> it }
) {
    val entries = mutableSetOf<String>()
    val creator = ParallelScatterZipCreator(
        ThreadPoolExecutor(
            NCPU,
            NCPU,
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory(),
            RejectedExecutionHandler { runnable, _ ->
                runnable.run()
            })
    )
    //将jar包里的文件序列化输出
    entries().asSequence().forEach { entry ->
        if (!entries.contains(entry.name)) {
            val zae = entryFactory(entry)

            val stream = InputStreamSupplier {
                when (entry.name.substringAfterLast('.', "")) {
                    "class" -> getInputStream(entry).use { src ->
                        try {
                            src.transform(transformer).inputStream()
                        } catch (e: Throwable) {
                            System.err.println("Broken class: ${this.name}!/${entry.name}")
                            getInputStream(entry)
                        }
                    }
                    else -> getInputStream(entry)
                }
            }

            creator.addArchiveEntry(zae, stream)
            entries.add(entry.name)
        } else {
            System.err.println("Duplicated jar entry: ${this.name}!/${entry.name}")
        }
    }
    val zip = ZipArchiveOutputStream(output)
    zip.use { zipStream ->
        try {
            creator.writeTo(zipStream)
            zipStream.close()
        } catch (e: Exception) {
            zipStream.close()
//            e.printStackTrace()
//            "e===>${e.message}".println()
            System.err.println("Duplicated jar entry: ${this.name}!")
        }
    }
}

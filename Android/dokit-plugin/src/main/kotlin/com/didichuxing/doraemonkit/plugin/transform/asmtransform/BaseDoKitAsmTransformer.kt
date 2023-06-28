package com.didichuxing.doraemonkit.plugin.transform.asmtransform

import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.Transformer
import com.didiglobal.booster.transform.asm.ClassTransformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/21-16:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
open class BaseDoKitAsmTransformer : Transformer {
    private val threadMxBean = ManagementFactory.getThreadMXBean()

    private val durations = mutableMapOf<ClassTransformer, Long>()

    private val classLoader: ClassLoader

    internal val transformers: Iterable<ClassTransformer>

    constructor() : this(Thread.currentThread().contextClassLoader)

    constructor(classLoader: ClassLoader = Thread.currentThread().contextClassLoader) : this(
        ServiceLoader.load(ClassTransformer::class.java, classLoader).sortedBy {
            it.javaClass.getAnnotation(Priority::class.java)?.value ?: 0
        }, classLoader
    )

    constructor(
        transformers: Iterable<ClassTransformer>,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader
    ) {
        this.classLoader = classLoader
        this.transformers = transformers
    }


    override fun onPreTransform(context: TransformContext) {
        this.transformers.forEach { transformer ->
            this.threadMxBean.sumCpuTime(transformer) {
                transformer.onPreTransform(context)
            }
        }
    }

    override fun transform(context: TransformContext, bytecode: ByteArray): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            this.transformers.fold(ClassNode().also { klass ->
                ClassReader(bytecode).accept(klass, 0)
            }) { klass, transformer ->
                this.threadMxBean.sumCpuTime(transformer) {
                    transformer.transform(context, klass)
                }
            }.accept(writer)
        }.toByteArray()
    }

    override fun onPostTransform(context: TransformContext) {
        this.transformers.forEach { transformer ->
            this.threadMxBean.sumCpuTime(transformer) {
                transformer.onPostTransform(context)
            }
        }

        val w1 = this.durations.keys.map {
            it.javaClass.name.length
        }.max() ?: 20
        this.durations.forEach { (transformer, ns) ->
            println("${transformer.javaClass.name.padEnd(w1 + 1)}: ${ns / 1000000} ms")
        }
    }

    private fun <R> ThreadMXBean.sumCpuTime(transformer: ClassTransformer, action: () -> R): R {
        val ct0 = this.currentThreadCpuTime
        val result = action()
        val ct1 = this.currentThreadCpuTime
        durations[transformer] = durations.getOrDefault(transformer, 0) + (ct1 - ct0)
        return result
    }

}


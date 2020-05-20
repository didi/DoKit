package com.didichuxing.doraemonkit.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.SecondaryInput
import com.android.build.api.transform.Status.ADDED
import com.android.build.api.transform.Status.CHANGED
import com.android.build.api.transform.Status.NOTCHANGED
import com.android.build.api.transform.Status.REMOVED
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.didiglobal.booster.gradle.*
import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.transform.AbstractKlassPool
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.artifacts
import com.didiglobal.booster.transform.util.transform
import java.io.File
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Represents a delegate of TransformInvocation
 *
 * @author johnsonlee
 */
internal class DoKitTransformInvocation(private val delegate: TransformInvocation, internal val transform: DoKitTransform) : TransformInvocation, TransformContext, ArtifactManager {

    private val executor = Executors.newWorkStealingPool(NCPU)

    override val name: String = delegate.context.variantName

    override val projectDir: File = delegate.project.projectDir

    override val buildDir: File = delegate.project.buildDir

    override val temporaryDir: File = delegate.context.temporaryDir

    override val reportsDir: File = File(buildDir, "reports").also { it.mkdirs() }

    override val bootClasspath = delegate.bootClasspath

    override val compileClasspath = delegate.compileClasspath

    override val runtimeClasspath = delegate.runtimeClasspath

    override val artifacts = this

    override val klassPool: AbstractKlassPool = object : AbstractKlassPool(compileClasspath, transform.bootKlassPool) {}

    override val applicationId = delegate.applicationId

    override val originalApplicationId = delegate.originalApplicationId

    override val isDebuggable = variant.buildType.isDebuggable

    override val isDataBindingEnabled = delegate.isDataBindingEnabled

    override fun hasProperty(name: String) = project.hasProperty(name)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getProperty(name: String, default: T): T = project.properties[name] as? T ?: default

    override fun getInputs(): MutableCollection<TransformInput> = delegate.inputs

    override fun getSecondaryInputs(): MutableCollection<SecondaryInput> = delegate.secondaryInputs

    override fun getReferencedInputs(): MutableCollection<TransformInput> = delegate.referencedInputs

    override fun isIncremental() = delegate.isIncremental

    override fun getOutputProvider(): TransformOutputProvider? = delegate.outputProvider

    override fun getContext(): Context = delegate.context

    override fun get(type: String) = variant.artifacts.get(type)

    internal fun doFullTransform() = doTransform(this::transformFully)

    internal fun doIncrementalTransform() = doTransform(this::transformIncrementally)

    private val tasks = mutableListOf<Future<*>>()

    private fun onPreTransform() {
        transform.transformers.forEach {
            it.onPreTransform(this)
        }
    }

    private fun onPostTransform() {
        tasks.forEach {
            it.get()
        }
        transform.transformers.forEach {
            it.onPostTransform(this)
        }
    }

    private fun doTransform(block: () -> Unit) {
        this.onPreTransform()
        block()
        this.onPostTransform()
    }

    private fun transformFully() {
        this.inputs.map {
            it.jarInputs + it.directoryInputs
        }.flatten().forEach { input ->
            tasks += executor.submit {
                val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
                outputProvider?.let { provider ->
                    project.logger.info("Transforming ${input.file}")
                    input.transform(provider.getContentLocation(input.name, input.contentTypes, input.scopes, format), this)
                }
            }
        }
    }

    private fun transformIncrementally() {
        this.inputs.parallelStream().forEach { input ->
            input.jarInputs.parallelStream().filter { it.status != NOTCHANGED }.forEach { jarInput ->
                tasks += executor.submit {
                    doIncrementalTransform(jarInput)
                }
            }
            input.directoryInputs.parallelStream().filter { it.changedFiles.isNotEmpty() }.forEach { dirInput ->
                val base = dirInput.file.toURI()
                tasks += executor.submit {
                    doIncrementalTransform(dirInput, base)
                }
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(jarInput: JarInput) {
        when (jarInput.status) {
            REMOVED -> jarInput.file.delete()
            CHANGED, ADDED -> {
                project.logger.info("Transforming ${jarInput.file}")
                outputProvider?.let { provider ->
                    jarInput.transform(provider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR), this)
                }
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(dirInput: DirectoryInput, base: URI) {
        dirInput.changedFiles.forEach { (file, status) ->
            when (status) {
                REMOVED -> {
                    project.logger.info("Deleting $file")
                    outputProvider?.let { provider ->
                        provider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY).parentFile.listFiles()?.asSequence()
                            ?.filter { it.isDirectory }
                            ?.map { File(it, dirInput.file.toURI().relativize(file.toURI()).path) }
                            ?.filter { it.exists() }
                            ?.forEach { it.delete() }
                    }
                    file.delete()
                }
                ADDED, CHANGED -> {
                    project.logger.info("Transforming $file")
                    outputProvider?.let { provider ->
                        val root = provider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                        val output = File(root, base.relativize(file.toURI()).path)
                        file.transform(output) { bytecode ->
                            bytecode.transform(this)
                        }
                    }
                }
            }
        }
    }
}

private fun ByteArray.transform(invocation: DoKitTransformInvocation): ByteArray {
    return invocation.transform.transformers.fold(this) { bytes, transformer ->
        transformer.transform(invocation, bytes)
    }
}

private fun QualifiedContent.transform(output: File, invocation: DoKitTransformInvocation) {
    this.file.transform(output) { bytecode ->
        bytecode.transform(invocation)
    }
}
package com.didichuxing.doraemonkit.plugin.transform

import com.android.build.api.transform.*
import com.android.build.api.transform.Status.*
import com.android.dex.DexFormat
import com.didichuxing.doraemonkit.plugin.dex
import com.didichuxing.doraemonkit.plugin.dokitTransform
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.gradle.*
import com.didiglobal.booster.kotlinx.NCPU
import com.didiglobal.booster.kotlinx.file
import com.didiglobal.booster.kotlinx.green
import com.didiglobal.booster.kotlinx.red
import com.didiglobal.booster.transform.*
import com.didiglobal.booster.transform.util.transform
import org.gradle.api.Project
import java.io.File
import java.net.URI
import java.util.concurrent.*

/**
 * Represents a delegate of TransformInvocation
 *
 * @author johnsonlee
 */
internal  class DoKitTransformInvocation(
    private val delegate: TransformInvocation,
    internal val transform: DoKitBaseTransform
) : TransformInvocation by delegate, TransformContext, ArtifactManager,DoKitTransformContext {

    private val project = transform.project

    private val outputs = CopyOnWriteArrayList<File>()

    override val name: String = delegate.context.variantName

    override val projectDir: File = project.projectDir

    override val buildDir: File = project.buildDir

    override val temporaryDir: File = delegate.context.temporaryDir

    override val reportsDir: File = File(buildDir, "reports").also { it.mkdirs() }

    override val bootClasspath = delegate.bootClasspath

    override val compileClasspath = delegate.compileClasspath

    override val runtimeClasspath = delegate.runtimeClasspath

    override val artifacts = this

    override val dependencies: Collection<String> by lazy {
        ResolvedArtifactResults(variant).map {
            it.id.displayName
        }
    }

    override val klassPool: AbstractKlassPool =
        object : AbstractKlassPool(compileClasspath, transform.bootKlassPool) {}

    override val applicationId = delegate.applicationId

    override val originalApplicationId = delegate.originalApplicationId

    override val isDebuggable = variant.buildType.isDebuggable

    override val isDataBindingEnabled = delegate.isDataBindingEnabled

    override fun hasProperty(name: String) = project.hasProperty(name)

    override fun <T> getProperty(name: String, default: T): T = project.getProperty(name, default)

    override fun get(type: String) = variant.artifacts.get(type)

    internal fun doFullTransform() = doTransform(this::transformFully)

    internal fun doIncrementalTransform() = doTransform(this::transformIncrementally)

    private fun onPreTransform() {
        transform.transformers.forEach {
            it.onPreTransform(this)
        }
    }

    private fun onPostTransform() {
        transform.transformers.forEach {
            it.onPostTransform(this)
        }
    }

    private fun doTransform(block: (ExecutorService) -> Iterable<Future<*>>) {
        this.outputs.clear()
        this.onPreTransform()

        val executor = Executors.newFixedThreadPool(NCPU)
        try {
            block(executor).forEach {
                it.get()
            }
        } finally {
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
        }

        this.onPostTransform()

        if (transform.verifyEnabled) {
            this.doVerify()
        }
    }

    private fun transformFully(executor: ExecutorService) = this.inputs.map {
        it.jarInputs + it.directoryInputs
    }.flatten().map { input ->
        executor.submit {
            val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
            outputProvider?.let { provider ->
                project.logger.info("Transforming ${input.file}")
                input.transform(
                    provider.getContentLocation(
                        input.name,
                        input.contentTypes,
                        input.scopes,
                        format
                    )
                )
            }
        }
    }

    private fun transformIncrementally(executor: ExecutorService) = this.inputs.map { input ->
        input.jarInputs.filter { it.status != NOTCHANGED }.map { jarInput ->
            executor.submit {
                doIncrementalTransform(jarInput)
            }
        } + input.directoryInputs.filter { it.changedFiles.isNotEmpty() }.map { dirInput ->
            val base = dirInput.file.toURI()
            executor.submit {
                doIncrementalTransform(dirInput, base)
            }
        }
    }.flatten()

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun doIncrementalTransform(jarInput: JarInput) {
        when (jarInput.status) {
            REMOVED -> jarInput.file.delete()
            CHANGED, ADDED -> {
                project.logger.info("Transforming ${jarInput.file}")
                outputProvider?.let { provider ->
                    jarInput.transform(
                        provider.getContentLocation(
                            jarInput.name,
                            jarInput.contentTypes,
                            jarInput.scopes,
                            Format.JAR
                        )
                    )
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
                        provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        ).parentFile.listFiles()?.asSequence()
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
                        val root = provider.getContentLocation(
                            dirInput.name,
                            dirInput.contentTypes,
                            dirInput.scopes,
                            Format.DIRECTORY
                        )
                        val output = File(root, base.relativize(file.toURI()).path)
                        outputs += output
                        file.transform(output) { bytecode ->
                            bytecode.transform()
                        }
                    }
                }
            }
        }
    }

    private fun doVerify() {
        outputs.sortedBy(File::nameWithoutExtension).forEach { output ->
            val out = temporaryDir.file(output.name)
            val rc = out.dex(
                output,
                variant.extension.defaultConfig.targetSdkVersion?.apiLevel
                    ?: DexFormat.API_NO_EXTENDED_OPCODES
            )
            println("${if (rc != 0) red("✗") else green("✓")} $output")
            out.deleteRecursively()
        }
    }

    private fun QualifiedContent.transform(output: File) {
        outputs += output
        try {
            this.file.dokitTransform(output) { bytecode ->
                bytecode.transform()
            }
        } catch (e: Exception) {
            "e===>${e.message}".println()
            e.printStackTrace()
        }

    }

    private fun ByteArray.transform(): ByteArray {
        return transform.transformers.fold(this) { bytes, transformer ->
            transformer.transform(this@DoKitTransformInvocation, bytes)
        }
    }

    override fun project(): Project {
        return project
    }

    override fun dokitExtension(): DoKitExtension {
        return project.extensions.getByType(DoKitExtension::class.java)
    }

    override fun <R> registerCollector(collector: Collector<R>) {
        TODO("Not yet implemented")
    }

    override fun <R> unregisterCollector(collector: Collector<R>) {
        TODO("Not yet implemented")
    }


}

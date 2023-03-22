package com.didichuxing.doraemonkit.plugin.transform

import com.android.build.api.variant.VariantInfo
import com.didichuxing.doraemonkit.plugin.transform.asmtransform.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.transform.classtransform.MSDClassTransformer
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer
 * @author johnsonlee
 */
open class DoKitDependTransformV34(androidProject: Project, private val level: Int) :
    DoKitBaseTransform(androidProject) {

    override val transformers = listOf<Transformer>(DoKitAsmTransformer(listOf(MSDClassTransformer(level))))

    override fun getName(): String {
        return "${this.javaClass.simpleName}_$level"
    }

    @Suppress("UnstableApiUsage")
    override fun applyToVariant(variant: VariantInfo): Boolean {
        return variant.buildTypeEnabled || (variant.flavorNames.isNotEmpty() && variant.fullVariantEnabled)
    }

    @Suppress("UnstableApiUsage")
    private val VariantInfo.fullVariantEnabled: Boolean
        get() = project.findProperty("booster.transform.${fullVariantName}.enabled")?.toString()
            ?.toBoolean() ?: true

    @Suppress("UnstableApiUsage")
    private val VariantInfo.buildTypeEnabled: Boolean
        get() = project.findProperty("booster.transform.${buildTypeName}.enabled")?.toString()
            ?.toBoolean() ?: true


}

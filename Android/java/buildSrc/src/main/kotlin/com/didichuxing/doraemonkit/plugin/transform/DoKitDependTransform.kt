package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.asmtransformer.DoKitAsmTransformer
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer
 * @author johnsonlee
 */
open class DoKitDependTransform(androidProject: Project, val level: Int) : DoKitBaseTransform(androidProject) {

    internal override val transformers = mutableListOf<Transformer>(DoKitAsmTransformer(level))

    override fun getName(): String {
        return "${this.javaClass.simpleName}_$level"
    }
}

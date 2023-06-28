package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.transform.asmtransform.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.transform.classtransform.MSDClassTransformer
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer
 * @author johnsonlee
 */
open class DoKitDependTransform(androidProject: Project, private val level: Int) :
    DoKitBaseTransform(androidProject) {

    override val transformers = listOf<Transformer>(DoKitAsmTransformer(listOf(MSDClassTransformer(level))))

    override fun getName(): String {
        return "${this.javaClass.simpleName}_$level"
    }
}

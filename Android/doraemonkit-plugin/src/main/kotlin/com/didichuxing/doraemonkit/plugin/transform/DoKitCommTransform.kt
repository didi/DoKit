package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.asmtransformer.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.BigImgTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.CommTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.EnterMethodStackTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.GlobalSlowMethodTransformer
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer、EnterMethodStackTransformer
 * @author johnsonlee
 */
class DoKitCommTransform(androidProject: Project) : DoKitBaseTransform(androidProject) {

    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                CommTransformer(),
                BigImgTransformer(),
                GlobalSlowMethodTransformer(),
                EnterMethodStackTransformer()
            )
        )
    )

}

package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.asmtransformer.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.BigImgClassTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.CommClassTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.EnterMSClassTransformer
import com.didichuxing.doraemonkit.plugin.classtransformer.GSMClassTransformer
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
                CommClassTransformer(),
                BigImgClassTransformer(),
                GSMClassTransformer(),
                EnterMSClassTransformer()
            )
        )
    )

}

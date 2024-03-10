package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.transform.asmtransform.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.transform.classtransform.*
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer、EnterMethodStackTransformer
 * @author johnsonlee
 */
class DoKitCommonTransform(androidProject: Project) : DoKitBaseTransform(androidProject) {

    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                CommClassTransformer(),
                ThirdLibsClassTransformer(),
                //网络
                Okhttp3ClassTransformer(),
                UrlConnectionTransformer(),
                WebViewClassTransformer(),

                //地图GPS
                GPSClassTransformer(),
                GPSAMapClassTransformer(),
                GPSBDClassTransformer(),
                GPSTencentClassTransformer(),
                //大图检测
                BigImgClassTransformer(),
                //全局慢函数
                GSMClassTransformer(),
                //入口慢函数
                EnterMSClassTransformer()
            )
        )
    )

}

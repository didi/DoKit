package com.didichuxing.doraemonkit.plugin.transform

import com.android.build.api.variant.VariantInfo
import com.didichuxing.doraemonkit.plugin.transform.asmtransform.DoKitAsmTransformer
import com.didichuxing.doraemonkit.plugin.transform.classtransform.*
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

class DoKitCommonTransformV34(project: Project) : DoKitBaseTransform(project) {


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

    @Suppress("UnstableApiUsage")
    override fun applyToVariant(variant: VariantInfo): Boolean {
        return variant.buildTypeEnabled || (variant.flavorNames.isNotEmpty() && variant.fullVariantEnabled)
    }

    @Suppress("UnstableApiUsage")
    private val VariantInfo.fullVariantEnabled: Boolean
        get() = project.findProperty("booster.transform.${fullVariantName}.enabled")?.toString()?.toBoolean() ?: true

    @Suppress("UnstableApiUsage")
    private val VariantInfo.buildTypeEnabled: Boolean
        get() = project.findProperty("booster.transform.${buildTypeName}.enabled")?.toString()?.toBoolean() ?: true

}


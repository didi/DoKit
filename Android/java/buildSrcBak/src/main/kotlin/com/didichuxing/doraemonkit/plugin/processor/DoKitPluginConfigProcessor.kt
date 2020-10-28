package com.didichuxing.doraemonkit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt
import com.didichuxing.doraemonkit.plugin.transform.*
import com.didiglobal.booster.gradle.*
import com.didiglobal.booster.kotlinx.get
import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.artifacts
import com.didiglobal.booster.transform.util.ComponentHandler
import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import javax.xml.parsers.SAXParserFactory

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/15-11:28
 * 描    述：
 * 修订历史：
 * ================================================
 */
@AutoService(VariantProcessor::class)
class DoKitPluginConfigProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
        if (!DoKitExtUtil.DOKIT_PLUGIN_SWITCH) {
            return
        }

        if (variant.isRelease()) {
            return
        }


        //查找application module下的配置
        if (variant is ApplicationVariant) {


//            if (DoKitExtUtil.OKHTTP_VERSION == "V3") {
//                variant.project.dependencies.add(
//                    "implementation",
//                    "com.didichuxing.doraemonkit:dokit-okhttp_v3:3.2.0"
//                )
//            } else if (DoKitExtUtil.OKHTTP_VERSION == "V4") {
//                variant.project.dependencies.add(
//                    "implementation",
//                    "com.didichuxing.doraemonkit:dokit-okhttp_v4:3.2.0"
//                )
//            }


            //查找AndroidManifest.xml 文件路径
            variant.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
                val parser = SAXParserFactory.newInstance().newSAXParser()
                val handler = ComponentHandler()
                parser.parse(manifest, handler)
                DoKitExtUtil.setApplications(handler.applications)
                "applications path====>${handler.applications}".println()
            }


            //读取插件配置
            variant.project.getAndroid<AppExtension>().let { appExt ->
                //查找Application路径
                val doKitExt = variant.project.extensions.getByType(DoKitExt::class.java)
                DoKitExtUtil.init(doKitExt, appExt.defaultConfig.applicationId!!)
            }

            //遍历三方库
            val dependencies = variant.dependencies
            DoKitExtUtil.THIRD_LIB_INFOS.clear()
            for (artifactResult: ResolvedArtifactResult in dependencies) {
                //println("三方库信息===>${artifactResult.variant.displayName}____${artifactResult.file.toString()}")
                val paths = artifactResult.file.toString().split("/")
                var fileName = ""
                if (paths.size >= 4) {
                    fileName = "${paths[paths.size - 4]}/${paths[paths.size - 1]}"
                } else {
                    fileName = paths[paths.size - 1]
                }
                val thirdLibInfo =
                    ThirdLibInfo(
                        fileName,
                        DoKitPluginUtil.FileSize(artifactResult.file)
                    )
                DoKitExtUtil.THIRD_LIB_INFOS.add(thirdLibInfo)
            }

        } else {
            "${variant.project.name}-不建议在Library Module下引入dokit插件".println()
        }

    }


}
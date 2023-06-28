package com.didichuxing.doraemonkit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.thirdlib.ThirdLibInfo
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.mergedManifests
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
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
class DoKitPluginConfigProcessor(val project: Project) : VariantProcessor {
    override fun process(variant: BaseVariant) {
        if (!DoKitExtUtil.DOKIT_PLUGIN_SWITCH) {
            return
        }

        if (variant.isRelease()) {
            return
        }

        //统计三方库信息
        if (DoKitExtUtil.THIRD_LIBINFO_SWITCH) {
            //遍历三方库
            val dependencies = variant.dependencies
            for (artifactResult: ResolvedArtifactResult in dependencies) {
                val variants = artifactResult.variant.displayName.split(" ")
                var thirdLibInfo: ThirdLibInfo? = null
                if (variants.size == 3) {
                    thirdLibInfo = ThirdLibInfo(variants[0], artifactResult.file.length())
                    checkConfig(thirdLibInfo.variant)
                } else if (variants.size == 4) {
                    thirdLibInfo = ThirdLibInfo("porject ${variants[1]}", artifactResult.file.length())
                    checkConfig(thirdLibInfo.variant)
                }
            }
        }
        //查找AndroidManifest.xml 文件 并处理
        processApplicationVariant(variant)

    }


    private fun checkConfig(variant: String) {
        if (variant.contains("dokitx-gps-mock") || variant.contains("dokit-gps-mock")) {
            DoKitExtUtil.DOKIT_GPS_MOCK_INCLUDE = true;
        }
    }


    private fun processApplicationVariant(variant: BaseVariant) {
        //查找application module下的配置
        if (variant is ApplicationVariant) {

            project.tasks.find {
                //"===task Name is ${it.name}".println()
                it.name == "processDebugManifest"
            }?.let { transformTask ->
                transformTask.doLast {
                    "===processDebugManifest task has executed===".println()
                    //查找AndroidManifest.xml 文件路径
                    variant.mergedManifests.forEach { manifest ->
                        val parser = SAXParserFactory.newInstance().newSAXParser()
                        val handler = DoKitComponentHandler()
                        "App Manifest path====>$manifest".println()
                        try {
                            parser.parse(manifest, handler)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "===processDebugManifest task error. ${manifest.absoluteFile}".println()
                        }
                        "App PackageName is====>${handler.appPackageName}".println()
                        "App Application path====>${handler.applications}".println()
                        DoKitExtUtil.setAppPackageName(handler.appPackageName)
                        DoKitExtUtil.setApplications(handler.applications)
                    }

                    //读取插件配置
                    variant.project.getAndroid<AppExtension>().let { appExt ->
                        //查找Application路径
                        val doKitExt = variant.project.extensions.getByType(DoKitExtension::class.java)
                        DoKitExtUtil.init(doKitExt)
                    }
                }
            }

        } else {
            "${variant.project.name}-不建议在Library Module下引入dokit插件".println()
        }

    }


}

package com.didichuxing.doraemonkit.plugin.processor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.artifacts
import com.didiglobal.booster.transform.util.ComponentHandler
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import java.lang.NullPointerException
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
            DoKitExtUtil.THIRD_LIB_INFOS.clear()
            for (artifactResult: ResolvedArtifactResult in dependencies) {
                //println("三方库信息===>${artifactResult.variant.displayName}____${artifactResult.file.toString()}")
                ///Users/didi/project/android/dokit_github/DoraemonKit/Android/java/app/libs/BaiduLBS_Android.jar
                ///Users/didi/.gradle/caches/modules-2/files-2.1/androidx.activity/activity-ktx/1.2.0/c16aac66e6c4617b01118ab2509f009bb7919b3b/activity-ktx-1.2.0.aar
                //println("三方库信息===>${artifactResult.variant.displayName}____${artifactResult.file.toString()}")
//                "artifactResult===>${artifactResult.file}|${artifactResult.variant}|${artifactResult.id}|${artifactResult.type}".println()
                //"artifactResult===>${artifactResult.variant.owner}|${artifactResult.variant.attributes}|${artifactResult.variant.displayName}|${artifactResult.variant.capabilities}|${artifactResult.variant.externalVariant}".println()
                //"artifactResult===>${artifactResult.variant.displayName}".println()
                val variants = artifactResult.variant.displayName.split(" ")
                var thirdLibInfo: ThirdLibInfo? = null
                if (variants.size == 3) {
                    thirdLibInfo = ThirdLibInfo(
                        variants[0],
                        artifactResult.file.length()
                    )
                    if (thirdLibInfo.variant.contains("dokitx-rpc")) {
                        DoKitExtUtil.HAS_DOKIT_RPC_MODULE = true
                    }
//                    "thirdLibInfo.variant===>${thirdLibInfo.variant}".println()
                    DoKitExtUtil.THIRD_LIB_INFOS.add(thirdLibInfo)
                } else if (variants.size == 4) {
                    thirdLibInfo = ThirdLibInfo(
                        "porject ${variants[1]}",
                        artifactResult.file.length()
                    )
                    if (thirdLibInfo.variant.contains("doraemonkit-rpc")) {
                        DoKitExtUtil.HAS_DOKIT_RPC_MODULE = true
                    }
//                    "thirdLibInfo.variant===>${thirdLibInfo.variant}".println()
                    DoKitExtUtil.THIRD_LIB_INFOS.add(thirdLibInfo)
                }


                //                val paths = artifactResult.file.toString().split("/")
//                var fileName: String = ""
//                var groupId: String = ""
//                var artifactId: String = ""
//                var version: String = ""
//                if (artifactResult.file.toString().contains(".gradle/caches")) {
//                    if (paths.size >= 5) {
//                        groupId = paths[paths.size - 5]
//                        artifactId = paths[paths.size - 4]
//                        version = paths[paths.size - 3]
//                        fileName =
//                            "$groupId:$artifactId:$version"
//                    } else {
//                        fileName = paths[paths.size - 1]
//                    }
//                } else {
//                    fileName = paths[paths.size - 1]
//                }
//
//                val thirdLibInfo =
//                    ThirdLibInfo(
//                        groupId,
//                        artifactId,
//                        version,
//                        fileName,
//                        artifactResult.file.length(),
//                        artifactResult.variant.displayName
//                    )
//                val key = "$groupId:$artifactId"
//                if (DoKitExtUtil.THIRD_LIB_INFOS[key] == null) {
//                    DoKitExtUtil.THIRD_LIB_INFOS[key] = thirdLibInfo
//                } else {
//                    val libInfo = DoKitExtUtil.THIRD_LIB_INFOS[key]
//                    if (DoKitPluginUtil.compareVersion(thirdLibInfo.version, libInfo!!.version) > 0) {
//                        DoKitExtUtil.THIRD_LIB_INFOS[key] = thirdLibInfo
//                    }
//                }
            }
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
                val handler = DoKitComponentHandler()
                "App Manifest path====>$manifest".println()
                parser.parse(manifest, handler)
                "App PackageName is====>${handler.appPackageName}".println()
                "App Application path====>${handler.applications}".println()
                DoKitExtUtil.setAppPackageName(handler.appPackageName)
                DoKitExtUtil.setApplications(handler.applications)
            }

            //读取插件配置
            variant.project.getAndroid<AppExtension>().let { appExt ->
                //查找Application路径
                val doKitExt = variant.project.extensions.getByType(DoKitExt::class.java)
                DoKitExtUtil.init(doKitExt)
//                "App ApplicationId is====>${appExt.defaultConfig.applicationId}".println()
//                appExt.defaultConfig.applicationId?.let {
//                    DoKitExtUtil.init(doKitExt)
//                } ?: throw NullPointerException("applicationId is null，applicationId暂不支持动态配置的方式读取")

            }

        } else {
            "${variant.project.name}-不建议在Library Module下引入dokit插件".println()
        }

    }


}
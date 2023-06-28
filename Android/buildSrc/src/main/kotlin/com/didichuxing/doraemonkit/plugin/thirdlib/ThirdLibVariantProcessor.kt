package com.didichuxing.doraemonkit.plugin.thirdlib

import com.android.build.gradle.api.BaseVariant
import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.isRelease
import com.didiglobal.booster.gradle.dependencies
import com.didiglobal.booster.task.spi.VariantProcessor
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult


/**
 * didi Create on 2023/3/22 .
 *
 * Copyright (c) 2023/3/22 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/22 2:57 下午
 * @Description 用一句话说明文件功能
 */

class ThirdLibVariantProcessor(val project: Project) : VariantProcessor {

    override fun process(variant: BaseVariant) {
        if (!DoKitExtUtil.DOKIT_PLUGIN_SWITCH) {
            return
        }

        if (variant.isRelease()) {
            return
        }

        val dokit = project.extensions.getByType(DoKitExtension::class.java)

        //统计三方库信息
        if (dokit.thirdLibEnable && DoKitExtUtil.THIRD_LIBINFO_SWITCH) {
            //遍历三方库
            val dependencies = variant.dependencies
            DoKitExtUtil.THIRD_LIB_INFOS.clear()
            for (artifactResult: ResolvedArtifactResult in dependencies) {
                val variants = artifactResult.variant.displayName.split(" ")
                var thirdLibInfo: ThirdLibInfo? = null
                if (variants.size == 3) {
                    thirdLibInfo = ThirdLibInfo(variants[0], artifactResult.file.length())
                    DoKitExtUtil.THIRD_LIB_INFOS.add(thirdLibInfo)
                } else if (variants.size == 4) {
                    thirdLibInfo = ThirdLibInfo("porject ${variants[1]}", artifactResult.file.length())
                    DoKitExtUtil.THIRD_LIB_INFOS.add(thirdLibInfo)
                }
            }
        }
    }
}

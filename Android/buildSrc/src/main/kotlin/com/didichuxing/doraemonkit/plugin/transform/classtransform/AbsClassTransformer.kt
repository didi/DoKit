package com.didichuxing.doraemonkit.plugin.transform.classtransform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.DoKitExtension
import com.didichuxing.doraemonkit.plugin.isRelease
import com.didichuxing.doraemonkit.plugin.transform.DoKitTransformContext
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import org.gradle.api.Project
import org.objectweb.asm.tree.ClassNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/5/12-18:06
 * 描    述：
 * 修订历史：
 * ================================================
 */

open class AbsClassTransformer : ClassTransformer {


    open fun onDoKitClassInterceptor(context: TransformContext, klass: ClassNode): Boolean {
        if (context.isRelease()) {
            return true
        }
        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return true
        }

        //过滤kotlin module-info
        if (klass.className == "module-info") {
            return true
        }
        return false
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (onDoKitClassInterceptor(context, klass)) {
            return klass
        }

        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
            return klass
        }

        if (context is DoKitTransformContext) {
            val project = context.project()
            val dokit = context.dokitExtension()
            transform(project, dokit, context, klass)
        }
        return klass
    }

    open fun transform(project: Project, dokit: DoKitExtension, context: TransformContext, klass: ClassNode): ClassNode = klass
}

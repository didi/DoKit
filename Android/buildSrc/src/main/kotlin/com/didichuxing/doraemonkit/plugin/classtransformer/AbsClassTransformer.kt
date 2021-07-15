package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.isRelease
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
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

    fun onCommInterceptor(context: TransformContext, klass: ClassNode): Boolean {
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
}
package com.didichuxing.doraemonkit.plugin.asmtransformer

import com.didichuxing.doraemonkit.plugin.classtransformer.MethodStackDepTransformer

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/21-16:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitAsmTransformer(private val level: Int) : BaseDoKitAsmTransformer(MethodStackDepTransformer(level)) {

}

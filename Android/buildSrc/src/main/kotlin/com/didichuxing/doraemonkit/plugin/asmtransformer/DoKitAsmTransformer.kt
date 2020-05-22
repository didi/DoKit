package com.didichuxing.doraemonkit.plugin.asmtransformer

import com.didichuxing.doraemonkit.plugin.asmclasstransformer.MethodStackDepTransformer
import com.didichuxing.doraemonkit.plugin.stack_method.MethodStackNodeUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/21-16:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitAsmTransformer(val level: Int) : BaseDoKitAsmTransformer(MethodStackDepTransformer(level)) {

}

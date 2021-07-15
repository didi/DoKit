package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import android.nfc.Tag
import okhttp3.Interceptor

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/13/21-13:18
 * 描    述： 不做具体实现 只用来做运行时判定 外部不需要继承
 * 修订历史：
 * ================================================
 */
abstract class AbsDoKitInterceptor : Interceptor {
    val TAG = this.javaClass.simpleName
}
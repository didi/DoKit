package com.didichuxing.doraemonkit.kit.network.rpc

import didihttp.Interceptor

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/13/21-17:27
 * 描    述：
 * 修订历史：不做具体实现 只用来做运行时判定 外部不需要继承
 * ================================================
 */
abstract class AbsDoKitRpcInterceptor : Interceptor {
    val TAG = this.javaClass.simpleName
}
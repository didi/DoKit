package com.didichuxing.doraemonkit.volley

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.didichuxing.doraemonkit.DoKit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/9/15-14:53
 * 描    述：
 * 修订历史：
 * ================================================
 */
object VolleyManager {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(DoKit.APPLICATION)
    }

    fun <T> add(request: Request<T>) {
        requestQueue.add(request)
    }


}
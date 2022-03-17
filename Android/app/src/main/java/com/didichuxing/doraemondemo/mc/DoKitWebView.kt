package com.didichuxing.doraemondemo.mc

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/7-19:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitWebView : WebView {
    companion object {
        const val TAG = "DoKitWebView"
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getAccessibilityClassName(): CharSequence {
        return super.getAccessibilityClassName()
    }

    override fun getAccessibilityTraversalBefore(): Int {
        return super.getAccessibilityTraversalBefore()
    }

}

package com.didichuxing.doraemonkit.kit.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-08-17:10
 * 描    述：自定义FrameLayout 用来区分原生FrameLayout
 * 修订历史：
 * ================================================
 */
open class DokitFrameLayout : FrameLayout, DokitViewInterface {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
package com.didichuxing.doraemonkit.widget.dropdown

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-11-19:17
 * 描    述：
 * 修订历史：
 * ================================================
 */
internal class Orientation(var mContext: Context) {
    private var unSelectedDrawable: Drawable? = null
    private var selectedDrawable: Drawable? = null
    private var orientation = 0
    fun getLeft(close: Boolean): Drawable? {
        return if (orientation == left) if (close) unSelectedDrawable else selectedDrawable else null
    }

    fun getTop(close: Boolean): Drawable? {
        return if (orientation == top) if (close) unSelectedDrawable else selectedDrawable else null
    }

    fun getRight(close: Boolean): Drawable? {
        return if (orientation == right) if (close) unSelectedDrawable else selectedDrawable else null
    }

    fun getBottom(close: Boolean): Drawable? {
        return if (orientation == bottom) if (close) unSelectedDrawable else selectedDrawable else null
    }

    /**
     * 初始化位置参数
     *
     * @param orientation
     * @param menuUnselectedIcon
     */
    fun init(orientation: Int, menuSelectedIcon: Int, menuUnselectedIcon: Int) {
        unSelectedDrawable = mContext.resources.getDrawable(menuUnselectedIcon)
        selectedDrawable = mContext.resources.getDrawable(menuSelectedIcon)
        this.orientation = orientation
    }

    companion object {
        const val left = 0
        const val top = 1
        const val right = 2
        const val bottom = 3
    }

}
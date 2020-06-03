package com.didichuxing.doraemonkit.kit.core

import android.view.ViewGroup
import android.view.WindowManager

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-10:57
 * 描    述：dokitView的初始化位置
 * 修订历史：
 * ================================================
 */
data class DokitViewLayoutParams(var flags: Int = 0,
                                 var gravity: Int = 0,//只针对系统悬浮窗起作用 值基本上为Gravity
                                 var x: Int = 0,
                                 var y: Int = 0,
                                 var width: Int = 0,
                                 var height: Int = 0) {


    companion object {
        /**
         * 悬浮窗不能获取焦点
         */
        const val FLAG_NOT_FOCUSABLE = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        /**
         * 悬浮窗不能获取焦点并且不相应触摸
         */
        const val FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

        const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

        const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
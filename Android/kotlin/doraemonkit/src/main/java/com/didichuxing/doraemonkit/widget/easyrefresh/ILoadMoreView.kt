package com.didichuxing.doraemonkit.widget.easyrefresh

import android.view.View

/**
 * Created by guanaj on 16/9/22.
 */
interface ILoadMoreView {
    /**
     * 重置
     */
    fun reset()

    /**
     * 加载中
     */
    fun loading()

    /**
     * 加载完成
     */
    fun loadComplete()
    fun loadFail()
    fun loadNothing()
    val canClickFailView: View
}
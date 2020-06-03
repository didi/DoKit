package com.didichuxing.doraemonkit.kit.core

import com.blankj.utilcode.util.ScreenUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-29-10:25
 * 描    述：保存上一次DokitView的位置信息
 * 修订历史：
 * ================================================
 */
class LastDokitViewPosInfo {
    val isPortrait = ScreenUtils.isPortrait()
    var dokitViewWidth = 0
    var dokitViewHeight = 0
    var leftMarginPercent = 0f
        private set
    var topMarginPercent = 0f
        private set


    fun setLeftMargin(leftMargin: Int) {
        leftMarginPercent = leftMargin.toFloat() / ScreenUtils.getAppScreenWidth().toFloat()
    }

    fun setTopMargin(topMargin: Int) {
        topMarginPercent = topMargin.toFloat() / ScreenUtils.getAppScreenHeight().toFloat()
    }

    override fun toString(): String {
        return "LastDokitViewPosInfo{" +
                "isPortrait=" + isPortrait +
                ", leftMarginPercent=" + leftMarginPercent +
                ", topMarginPercent=" + topMarginPercent +
                '}'
    }
}
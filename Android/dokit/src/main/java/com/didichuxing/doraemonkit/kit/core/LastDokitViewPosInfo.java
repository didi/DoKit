package com.didichuxing.doraemonkit.kit.core;

import com.didichuxing.doraemonkit.util.ScreenUtils;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-29-10:25
 * 描    述：保存上一次DokitView的位置信息
 * 修订历史：
 * ================================================
 */
public class LastDokitViewPosInfo {
    private boolean isPortrait = true;
    private int dokitViewWidth;
    private int dokitViewHeight;
    private float leftMarginPercent;
    private float topMarginPercent;

    public int getDokitViewWidth() {
        return dokitViewWidth;
    }

    public void setDokitViewWidth(int dokitViewWidth) {
        this.dokitViewWidth = dokitViewWidth;
    }

    public int getDokitViewHeight() {
        return dokitViewHeight;
    }

    public void setDokitViewHeight(int dokitViewHeight) {
        this.dokitViewHeight = dokitViewHeight;
    }

    public void setPortrait() {
        this.isPortrait = ScreenUtils.isPortrait();
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMarginPercent = (float) leftMargin / (float) ScreenUtils.getAppScreenWidth();
    }

    public void setTopMargin(int topMargin) {
        this.topMarginPercent = (float) topMargin / (float) ScreenUtils.getAppScreenHeight();
    }

    public boolean isPortrait() {
        return isPortrait;
    }

    public float getLeftMarginPercent() {
        return leftMarginPercent;
    }

    public float getTopMarginPercent() {
        return topMarginPercent;
    }

    @Override
    public String toString() {
        return "LastDokitViewPosInfo{" +
                "isPortrait=" + isPortrait +
                ", leftMarginPercent=" + leftMarginPercent +
                ", topMarginPercent=" + topMarginPercent +
                '}';
    }
}

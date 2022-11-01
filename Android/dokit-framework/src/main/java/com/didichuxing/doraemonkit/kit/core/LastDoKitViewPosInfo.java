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
public class LastDoKitViewPosInfo {
    private boolean isPortrait = true;
    private int doKitViewWidth;
    private int doKitViewHeight;
    private float leftMarginPercent;
    private float topMarginPercent;

    public int getDoKitViewWidth() {
        return doKitViewWidth;
    }

    public void setDoKitViewWidth(int doKitViewWidth) {
        this.doKitViewWidth = doKitViewWidth;
    }

    public int getDoKitViewHeight() {
        return doKitViewHeight;
    }

    public void setDoKitViewHeight(int doKitViewHeight) {
        this.doKitViewHeight = doKitViewHeight;
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

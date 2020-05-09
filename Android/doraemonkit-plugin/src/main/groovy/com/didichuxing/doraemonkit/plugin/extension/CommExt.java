package com.didichuxing.doraemonkit.plugin.extension;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/28-14:56
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CommExt {

    /**
     * 地图经纬度开关
     */
    public boolean gpsSwitch = true;
    /**
     * 网络开关
     */
    public boolean networkSwitch = true;
    /**
     * 大图开关
     */
    public boolean bigImgSwitch = true;


    public void gpsSwitch(boolean mapSwitch) {
        this.gpsSwitch = mapSwitch;
    }

    public void networkSwitch(boolean networkSwitch) {
        this.networkSwitch = networkSwitch;
    }

    public void bigImgSwitch(boolean bigImgSwitch) {
        this.bigImgSwitch = bigImgSwitch;
    }

    @Override
    public String toString() {
        return "CommExt{" +
                "gpsSwitch=" + gpsSwitch +
                ", networkSwitch=" + networkSwitch +
                ", bigImgSwitch=" + bigImgSwitch +
                '}';
    }
}

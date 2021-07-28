package com.didichuxing.doraemonkit.kit.network.bean;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/19-14:45
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class WhiteHostBean {
    private String host;
    private boolean canAdd;

    public WhiteHostBean(String host, boolean canAdd) {
        this.host = host;
        this.canAdd = canAdd;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public void setCanAdd(boolean canAdd) {
        this.canAdd = canAdd;
    }
}

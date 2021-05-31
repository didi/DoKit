package com.didichuxing.doraemonkit.kit.network.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 请求bean
 */
public class Request implements Serializable {

    public String url;

    public String method;

    public String headers;

    public String postData;

    public String encode;

    @Override
    public String toString() {
        return "";
    }

    public boolean filter(String text) {
        return false;
    }
}

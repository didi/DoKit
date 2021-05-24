package com.didichuxing.doraemonkit.kit.network.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * @desc: 一条网络请求记录
 */
public class NetworkRecord implements Serializable {
    private static final String METHOD_GET = "get";
    private static final String METHOD_POST = "post";

    public int mRequestId;
    public Request mRequest;
    public Response mResponse;
    public String mPlatform;
    public String mResponseBody;

    public long requestLength;
    public long responseLength;

    public long startTime;
    public long endTime;

    public boolean filter(String text) {

        return false;
    }


    public boolean isGetRecord() {
        return false;
    }

    public boolean isPostRecord() {
        return false;
    }
}

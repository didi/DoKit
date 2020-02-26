package com.didichuxing.doraemonkit.kit.network.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import java.io.Serializable;
import org.json.JSONObject;

/**
 * @desc: 一条网络请求记录
 */
public class NetworkRecord implements Serializable {
    private static final String METHOD_GET = "get";
    private static final String METHOD_POST = "post";

    public int mRequestId;
    public Request mRequest;
    public Response mResponse;
    public String mResponseBody;
    public String prettyResponse;

    public long requestLength;
    public long responseLength;

    public long startTime;
    public long endTime;

    public boolean filter(String text) {
        // 目前只支持url筛选，后续需要再扩展
        if (mRequest != null && mRequest.filter(text)) {
            return true;
        }
        return false;
    }


    public boolean isGetRecord() {
        return mRequest != null && mRequest.method != null && TextUtils.equals(METHOD_GET, mRequest.method.toLowerCase());
    }

    public boolean isPostRecord() {
        return mRequest != null && mRequest.method != null && TextUtils.equals(METHOD_POST, mRequest.method.toLowerCase());
    }

    public String responseBody() {
        if (TextUtils.isEmpty(prettyResponse)){
            try {
                JSONObject json = new JSONObject(mResponseBody);
                prettyResponse = json.toString(2);
            }catch (Exception e){
                prettyResponse = mResponseBody;
            }
        }
        return prettyResponse;

    }
}

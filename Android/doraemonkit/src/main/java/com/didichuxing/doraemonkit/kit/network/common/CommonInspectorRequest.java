package com.didichuxing.doraemonkit.kit.network.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;

import java.util.List;

/**
 * @desc: 抓包通用请求bean
 */
public class CommonInspectorRequest implements NetworkInterpreter.InspectorRequest {
    private final int mRequestId;
    private final String url;
    private final String method;
    private final String body;
    private final CommonHeaders headers;

    public CommonInspectorRequest(
            int requestId,
            @NonNull String url,
            @NonNull String method,
            @Nullable String body,
            @Nullable CommonHeaders headers) {
        mRequestId = requestId;
        this.url = url;
        this.method = method;
        this.body = body;
        this.headers = headers;
    }

    @Override
    public int id() {
        return mRequestId;
    }


    @Override
    public String url() {
        return url;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public byte[] body() {
        if (TextUtils.isEmpty(body)) {
            return null;
        }
        return body.getBytes();
    }

    @Override
    public int headerCount() {
        if (headers != null) {
            return headers.size();
        }
        return 0;
    }

    @Override
    public String headerName(int index) {
        if (headers != null) {
            return headers.name(index);
        }
        return null;
    }

    @Override
    public String headerValue(int index) {
        if (headers != null) {
            return headers.value(index);
        }
        return null;
    }

    @Override
    public String firstHeaderValue(String name) {
        if (headers != null) {
            List<String> values = headers.values(name);
            if (values != null && values.size() > 0) {
                return values.get(0);
            }
        }
        return null;
    }
}

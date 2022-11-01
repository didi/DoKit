package com.didichuxing.doraemonkit.kit.network.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;

import java.util.List;

public class CommonInspectorResponse implements NetworkInterpreter.InspectorResponse {
    private final int mRequestId;
    private final String url;
    private final int statusCode;
    private final CommonHeaders headers;

    public CommonInspectorResponse(
            int requestId,
            @NonNull String url,
            int statusCode,
            @Nullable CommonHeaders headers) {
        mRequestId = requestId;
        this.url = url;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    @Override
    public int requestId() {
        return mRequestId;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public int statusCode() {
        return statusCode;
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


/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class URLConnectionInspectorRequest
        extends URLConnectionInspectorHeaders
        implements NetworkInterpreter.InspectorRequest {
    private final int mRequestId;
    private SimpleRequestEntity mRequestEntity;
    private final RequestBodyHelper mRequestBodyHelper;
    private final String mUrl;
    private final String mMethod;

    public URLConnectionInspectorRequest(
            int requestId,
            ArrayList<Pair<String, String>> header,
            HttpURLConnection configuredRequest,
            RequestBodyHelper requestBodyHelper) {
        super(header);
        mRequestId = requestId;
        mRequestBodyHelper = requestBodyHelper;
        mUrl = configuredRequest.getURL().toString();
        mMethod = configuredRequest.getRequestMethod();
    }

    public void setRequestEntity(SimpleRequestEntity entity) {
        mRequestEntity = entity;
    }

    @Override
    public int id() {
        return mRequestId;
    }

    @Override
    public String url() {
        return mUrl;
    }

    @Override
    public String method() {
        return mMethod;
    }

    @Nullable
    @Override
    public byte[] body() throws IOException {
        if (mRequestEntity != null) {
            OutputStream out = mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"));
            try {
                mRequestEntity.writeTo(out);
            } finally {
                out.close();
            }
            return mRequestBodyHelper.getDisplayBody();
        } else {
            return null;
        }
    }
}

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.httpurlconnection.inspector;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;

import java.io.IOException;
import java.util.ArrayList;


public class URLConnectionInspectorRequest
        extends URLConnectionInspectorHeaders
        implements NetworkInterpreter.InspectorRequest {
    private final int mRequestId;
    private final String mUrl;
    private final String mMethod;

    public URLConnectionInspectorRequest(
            int requestId,
            ArrayList<Pair<String, String>> header,
            String url,
            String method) {
        super(header);
        mRequestId = requestId;
        mUrl = url;
        mMethod = method;
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
        return null;
    }
}

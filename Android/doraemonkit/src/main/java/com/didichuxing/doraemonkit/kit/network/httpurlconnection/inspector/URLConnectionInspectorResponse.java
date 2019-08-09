/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.httpurlconnection.inspector;

import android.util.Pair;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;

import java.util.ArrayList;

public class URLConnectionInspectorResponse
        extends URLConnectionInspectorHeaders
        implements NetworkInterpreter.InspectorResponse {
    private final int mRequestId;
    private final String mUrl;
    private final int mStatusCode;

    public URLConnectionInspectorResponse(int requestId, ArrayList<Pair<String, String>> header, String url, int statusCode) {
        super(header);
        mRequestId = requestId;
        mUrl = url;
        mStatusCode = statusCode;
    }

    @Override
    public int requestId() {
        return mRequestId;
    }

    @Override
    public String url() {
        return mUrl;
    }

    @Override
    public int statusCode() {
        return mStatusCode;
    }
}

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.utils.StreamUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

public class URLConnectionInspectorResponse
        extends URLConnectionInspectorHeaders
        implements NetworkInterpreter.InspectorResponse {
    private final int mRequestId;
    private final String mUrl;
    private final int mStatusCode;

    public URLConnectionInspectorResponse(int requestId, HttpURLConnection conn, int statusCode) throws IOException {
        super(StreamUtil.convertHeaders(conn.getHeaderFields()));
        mRequestId = requestId;
        mUrl = conn.getURL().toString();
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

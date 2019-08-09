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

public class URLConnectionInspectorHeaders implements NetworkInterpreter.InspectorHeaders {
    private final ArrayList<Pair<String, String>> mHeaders;

    public URLConnectionInspectorHeaders(ArrayList<Pair<String, String>> headers) {
        mHeaders = headers;
    }

    @Override
    public int headerCount() {
        return mHeaders.size();
    }

    @Override
    public String headerName(int index) {
        return mHeaders.get(index).first;
    }

    @Override
    public String headerValue(int index) {
        return mHeaders.get(index).second;
    }

    @Override
    public String firstHeaderValue(String name) {
        int N = headerCount();
        for (int i = 0; i < N; i++) {
            if (name.equalsIgnoreCase(headerName(i))) {
                return headerValue(i);
            }
        }
        return null;
    }
}

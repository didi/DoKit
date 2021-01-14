/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;


import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DefaultResponseHandler implements ResponseHandler {
    private final NetworkInterpreter mNetworkInterpreter;
    private final int mRequestId;
    private NetworkRecord mRecord;


    public DefaultResponseHandler(NetworkInterpreter networkInterpreter, int requestId, NetworkRecord record) {
        mNetworkInterpreter = networkInterpreter;
        mRequestId = requestId;
        mRecord = record;
    }

    @Override
    public void onEOF(ByteArrayOutputStream outputStream) {
        mNetworkInterpreter.responseReadFinished(mRequestId, mRecord, outputStream);
    }

    @Override
    public void onError(IOException e) {
        mNetworkInterpreter.responseReadFailed(mRequestId, e.toString());
    }
}

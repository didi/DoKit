/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.core

import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import java.io.ByteArrayOutputStream
import java.io.IOException

class DefaultResponseHandler(private val mNetworkInterpreter: NetworkInterpreter, private val mRequestId: Int, private val mRecord: NetworkRecord) : ResponseHandler {
    override fun onEOF(outputStream: ByteArrayOutputStream?) {
        mNetworkInterpreter.responseReadFinished(mRequestId, mRecord, outputStream)
    }

    override fun onError(e: IOException) {
        mNetworkInterpreter.responseReadFailed(mRequestId, e.toString())
    }

}
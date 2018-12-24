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
import android.util.Log;
import android.util.Pair;

import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;
import com.didichuxing.doraemonkit.kit.network.utils.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class URLConnectionManager {
    public static final String TAG = "URLConnectionManager";
    private final NetworkInterpreter mInterpreter = NetworkInterpreter.get();
    private final int mRequestId;
    private NetworkRecord mRecord;
    private HttpURLConnection mConnection;
    @Nullable
    private URLConnectionInspectorRequest mInspectorRequest;
    @Nullable
    private RequestBodyHelper mRequestBodyHelper;
    public ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();

    public URLConnectionManager() {
        mRequestId = mInterpreter.nextRequestId();
    }


    /**
     */
    public void preConnect(HttpURLConnection connection) {
        throwIfConnection();
        mConnection = connection;
        mRequestBodyHelper = new RequestBodyHelper();
        ArrayList<Pair<String, String>> header;
        // connect参数不知道什么会被置为true，以防崩溃，这里直接try-catch住
        try {
            header = StreamUtil.convertHeaders(connection.getRequestProperties());
        } catch (Exception e) {
            Log.e(TAG, "get head exception", e);
            header = new ArrayList<>();
        }
        mInspectorRequest = new URLConnectionInspectorRequest(
                getStethoRequestId(),
                header,
                connection,
                mRequestBodyHelper);
        mRecord = mInterpreter.createRecord(mRequestId, mInspectorRequest);
    }


    public void postConnect(int statusCode) throws IOException {
        throwIfNoConnection();
        URLConnectionInspectorResponse response = new URLConnectionInspectorResponse(
                getStethoRequestId(),
                mConnection,
                statusCode);
        mInterpreter.fetchResponseInfo(mRecord, response);

    }

    public void fetchRequestBody() {
        if (mInspectorRequest != null && mRecord != null) {
            SimpleRequestEntity entity = new ByteArrayRequestEntity(mOutputStream.toByteArray());
            mInspectorRequest.setRequestEntity(entity);
            mInterpreter.fetRequestBody(mRecord, mInspectorRequest);
        }
    }

    public void httpExchangeFailed(IOException ex) {
        throwIfNoConnection();
        mInterpreter.httpExchangeFailed(getStethoRequestId(), ex.toString());
    }

    public InputStream interpretResponseStream(@Nullable InputStream responseStream) {
        throwIfNoConnection();
        // Note that Content-Encoding is stripped out by HttpURLConnection on modern versions of
        // Android (fun fact, it's powered by okhttp) when decompression is handled transparently.
        // When this occurs, we will not be able to report the compressed size properly.  Callers,
        // however, can disable this behaviour which will once again give us access to the raw
        // Content-Encoding so that we can handle it properly.
        responseStream = mInterpreter.interpretResponseStream(
                mRecord,
                mRequestId,
                mConnection.getHeaderField("Content-Type"),
                responseStream,
                new DefaultResponseHandler(mInterpreter, getStethoRequestId(), mRecord));
        return responseStream;
    }


    private void throwIfNoConnection() {
        if (mConnection == null) {
            throw new IllegalStateException("Must call preConnect");
        }
    }

    private void throwIfConnection() {
        if (mConnection != null) {
            throw new IllegalStateException("Must not call preConnect twice");
        }
    }

    public NetworkInterpreter getInterpreter() {
        return mInterpreter;
    }

    public int getStethoRequestId() {
        return mRequestId;
    }
}

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.rpc;


import com.blankj.utilcode.util.ConvertUtils;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.core.ResponseHandler;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockResponseHandler implements ResponseHandler {

    private static final String TAG = "MockResponseHandler";
    private String host;
    private MockTemplateApiBean mockApi;

    public MockResponseHandler(String host, MockTemplateApiBean mockApi) {
        this.host = host;
        this.mockApi = mockApi;
    }

    /**
     * response读取完成
     *
     * @param outputStream
     */
    @Override
    public void onEOF(ByteArrayOutputStream outputStream) {
        if (host.equals(NetworkManager.MOCK_HOST)) {
            mockApi.setResponseFrom(MockTemplateApiBean.RESPONSE_FROM_MOCK);
        } else {
            mockApi.setResponseFrom(MockTemplateApiBean.RESPONSE_FROM_REAL);
        }
        String strResponseBody = ConvertUtils.outputStream2String(outputStream, "utf-8");
        mockApi.setStrResponse(strResponseBody);
        //更新本地数据库
        DokitDbManager.getInstance().updateTemplateApi(mockApi);
        //LogHelper.i(TAG, "result===>" + ConvertUtils.outputStream2String(outputStream, "utf-8"));
    }

    @Override
    public void onError(IOException e) {
        LogHelper.i(TAG, "error===>" + e.getMessage());
    }
}

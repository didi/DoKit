/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.abridge.IBridge;
import com.squareup.leakcanary.internal.DisplayLeakActivity;

/**
 * Logs leak analysis results, and then shows a notification which will start {@link
 * DisplayLeakActivity}.
 * <p>
 * You can extend this class and override {@link #afterDefaultHandling(HeapDump, AnalysisResult,
 * String)} to add custom behavior, e.g. uploading the heap dump.
 */
public class UploadLeakService extends DisplayLeakService {
    private static final String TAG = "UploadLeakService";


    /**
     * 进行上传服务
     *
     * @param heapDump
     * @param result
     * @param leakInfo
     */
    @Override
    protected void afterDefaultHandling(@NonNull HeapDump heapDump, @NonNull AnalysisResult result, @NonNull String leakInfo) {
        super.afterDefaultHandling(heapDump, result, leakInfo);
        if (TextUtils.isEmpty(leakInfo)) {
            return;
        }
        Log.i(TAG, "====leakInfo====" + leakInfo);
        try {
            IBridge.sendAIDLMessage(leakInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

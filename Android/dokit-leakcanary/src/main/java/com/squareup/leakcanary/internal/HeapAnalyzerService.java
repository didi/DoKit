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
package com.squareup.leakcanary.internal;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.didichuxing.doraemonkit.util.Utils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.squareup.leakcanary.R;
import com.squareup.leakcanary.AbstractAnalysisResultService;
import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.AnalyzerProgressListener;
import com.squareup.leakcanary.CanaryLog;
import com.squareup.leakcanary.HeapAnalyzer;
import com.squareup.leakcanary.HeapDump;

/**
 * This service runs in a separate process to avoid slowing down the app process or making it run
 * out of memory.
 * leakCanary 堆内存分下前台服务
 */
public final class HeapAnalyzerService extends ForegroundService
        implements AnalyzerProgressListener {
    private static final String TAG = "HeapAnalyzerService";
    private static final String LISTENER_CLASS_EXTRA = "listener_class_extra";
    private static final String HEAPDUMP_EXTRA = "heapdump_extra";

    /**
     * 时候过滤掉dokit sdk 导致的内存泄漏
     */
    private boolean isIgnoreDokit = true;

    /**
     * 启动当前服务
     *
     * @param context
     * @param heapDump
     * @param listenerServiceClass
     */
    public static void runAnalysis(Context context, HeapDump heapDump,
                                   Class<? extends AbstractAnalysisResultService> listenerServiceClass) {
        LeakCanaryInternals.setEnabledBlocking(context, HeapAnalyzerService.class, true);
        LeakCanaryInternals.setEnabledBlocking(context, listenerServiceClass, true);
        Intent intent = new Intent(context, HeapAnalyzerService.class);
        intent.putExtra(LISTENER_CLASS_EXTRA, listenerServiceClass.getName());
        intent.putExtra(HEAPDUMP_EXTRA, heapDump);
        ContextCompat.startForegroundService(context, intent);
    }

    public HeapAnalyzerService() {
        super(HeapAnalyzerService.class.getSimpleName(), R.string.leak_canary_notification_analysing);
//        Utils.init(getApplication());
    }

    /**
     * 服务启动时调用
     *
     * @param intent
     */
    @Override
    protected void onHandleIntentInForeground(@Nullable Intent intent) {
        if (intent == null) {
            CanaryLog.d("HeapAnalyzerService received a null intent, ignoring.");
            return;
        }
        //接受外部传递进来参数
        String listenerClassName = intent.getStringExtra(LISTENER_CLASS_EXTRA);
        HeapDump heapDump = (HeapDump) intent.getSerializableExtra(HEAPDUMP_EXTRA);

        HeapAnalyzer heapAnalyzer =
                new HeapAnalyzer(heapDump.excludedRefs, this, heapDump.reachabilityInspectorClasses);

//        Instance instance = heapAnalyzer.preCheckForLeak(heapDump.heapDumpFile, heapDump.referenceKey);
//        String leakClassName = instance.getClassObj().getClassName();
//        LogHelper.i(TAG, "====leakClassName====>" + leakClassName);
//        //过滤掉dokit内部的内存泄漏
//        if (isIgnoreDokit && !TextUtils.isEmpty(leakClassName) && leakClassName.contains("com.didichuxing.doraemonkit")) {
//            return;
//        }

        /**
         * 检查对象是否没有被回收
         */
        AnalysisResult result = heapAnalyzer.checkForLeak(heapDump.heapDumpFile, heapDump.referenceKey,
                heapDump.computeRetainedHeapSize);

        AbstractAnalysisResultService.sendResultToListener(this, listenerClassName, heapDump, result);
    }


    @Override
    public void onProgressUpdate(Step step) {
        int percent = (int) ((100f * step.ordinal()) / Step.values().length);
        CanaryLog.d("Analysis in progress, working on: %s", step.name());
        String lowercase = step.name().replace("_", " ").toLowerCase();
        String message = lowercase.substring(0, 1).toUpperCase() + lowercase.substring(1);
        showForegroundNotification(100, percent, false, message);
    }
}

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
package com.squareup.leakcanary.internal

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.squareup.leakcanary.*

/**
 * This service runs in a separate process to avoid slowing down the app process or making it run
 * out of memory.
 * leakCanary 堆内存分下前台服务
 */
class HeapAnalyzerService : ForegroundService(HeapAnalyzerService::class.java.simpleName, R.string.leak_canary_notification_analysing), AnalyzerProgressListener {
    /**
     * 时候过滤掉dokit sdk 导致的内存泄漏
     */
    private val isIgnoreDokit = true

    /**
     * 服务启动时调用
     *
     * @param intent
     */
    override fun onHandleIntentInForeground(intent: Intent?) {
        if (intent == null) {
            CanaryLog.d("HeapAnalyzerService received a null intent, ignoring.")
            return
        }
        //接受外部传递进来参数
        val listenerClassName = intent.getStringExtra(LISTENER_CLASS_EXTRA)
        val heapDump = intent.getSerializableExtra(HEAPDUMP_EXTRA) as HeapDump
        val heapAnalyzer = HeapAnalyzer(heapDump.excludedRefs, this, heapDump.reachabilityInspectorClasses)

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
        val result = heapAnalyzer.checkForLeak(heapDump.heapDumpFile, heapDump.referenceKey,
                heapDump.computeRetainedHeapSize)
        AbstractAnalysisResultService.sendResultToListener(this, listenerClassName, heapDump, result)
    }

    override fun onProgressUpdate(step: AnalyzerProgressListener.Step) {
        val percent = (100f * step.ordinal / AnalyzerProgressListener.Step.values().size).toInt()
        CanaryLog.d("Analysis in progress, working on: %s", step.name)
        val lowercase = step.name.replace("_", " ").toLowerCase()
        val message = lowercase.substring(0, 1).toUpperCase() + lowercase.substring(1)
        showForegroundNotification(100, percent, false, message)
    }

    companion object {
        private const val TAG = "HeapAnalyzerService"
        private const val LISTENER_CLASS_EXTRA = "listener_class_extra"
        private const val HEAPDUMP_EXTRA = "heapdump_extra"

        /**
         * 启动当前服务
         *
         * @param context
         * @param heapDump
         * @param listenerServiceClass
         */
        fun runAnalysis(context: Context, heapDump: HeapDump?,
                        listenerServiceClass: Class<out AbstractAnalysisResultService?>) {
            LeakCanaryInternals.setEnabledBlocking(context, HeapAnalyzerService::class.java, true)
            LeakCanaryInternals.setEnabledBlocking(context, listenerServiceClass, true)
            val intent = Intent(context, HeapAnalyzerService::class.java)
            intent.putExtra(LISTENER_CLASS_EXTRA, listenerServiceClass.name)
            intent.putExtra(HEAPDUMP_EXTRA, heapDump)
            ContextCompat.startForegroundService(context!!, intent)
        }
    }
}
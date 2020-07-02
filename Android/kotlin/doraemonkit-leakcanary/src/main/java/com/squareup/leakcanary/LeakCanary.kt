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
package com.squareup.leakcanary

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.text.format.Formatter
import android.util.Log
import com.squareup.leakcanary.AndroidExcludedRefs.createAppDefaults
import com.squareup.leakcanary.UploadLeakService
import com.squareup.leakcanary.internal.DisplayLeakActivity
import com.squareup.leakcanary.internal.HeapAnalyzerService
import com.squareup.leakcanary.internal.LeakCanaryInternals
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.isInServiceProcess
import com.squareup.leakcanary.internal.LeakCanaryInternals.Companion.setEnabledBlocking

class LeakCanary private constructor() {
    companion object {
        /**
         * Creates a [RefWatcher] that works out of the box, and starts watching activity
         * references (on ICS+).
         */
        fun install(application: Application): RefWatcher =
            //DisplayLeakService 服务的名称
            refWatcher(application).listenerServiceClass(UploadLeakService::class.java)
                    .excludedRefs(createAppDefaults()
                            .build())
                    ?.buildAndInstall()!!

        /**
         * Returns the [RefWatcher] installed via
         * [AndroidRefWatcherBuilder.buildAndInstall], and [RefWatcher.DISABLED] is no
         * [RefWatcher] has been installed.
         */
        fun installedRefWatcher(): RefWatcher {
            return LeakCanaryInternals.installedRefWatcher ?: return RefWatcher.DISABLED
        }

        fun refWatcher(context: Context): AndroidRefWatcherBuilder {
            return AndroidRefWatcherBuilder(context)
        }

        /**
         * Blocking inter process call that enables the [DisplayLeakActivity]. When you first
         * install the app, [DisplayLeakActivity] is enabled by default if LeakCanary is configured
         * to use [DisplayLeakService]. You can call this method to enable
         * [DisplayLeakActivity] manually.
         */
        fun enableDisplayLeakActivity(context: Context) {
            setEnabledBlocking(context, DisplayLeakActivity::class.java, true)
        }

        @Deprecated("Use {@link #setLeakDirectoryProvider(LeakDirectoryProvider)} instead.")
        fun setDisplayLeakActivityDirectoryProvider(
                leakDirectoryProvider: LeakDirectoryProvider) {
            setLeakDirectoryProvider(leakDirectoryProvider)
        }

        /**
         * Used to customize the location for the storage of heap dumps. The default implementation is
         * [DefaultLeakDirectoryProvider].
         *
         * @throws IllegalStateException if a LeakDirectoryProvider has already been set, including
         * if the default has been automatically set when installing the ref watcher.
         */
        fun setLeakDirectoryProvider(
                leakDirectoryProvider: LeakDirectoryProvider) {
            LeakCanaryInternals.setLeakDirectoryProvider(leakDirectoryProvider)
        }

        /**
         * Returns a string representation of the result of a heap analysis.
         */
        @JvmStatic
        fun leakInfo(context: Context,
                     heapDump: HeapDump,
                     result: AnalysisResult,
                     detailed: Boolean): String {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val packageInfo: PackageInfo
            packageInfo = try {
                packageManager.getPackageInfo(packageName, 0)
            } catch (e: NameNotFoundException) {
                throw RuntimeException(e)
            }
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            var info = "In $packageName:$versionName:$versionCode.\n"
            var detailedString = ""
            if (result.leakFound) {
                if (result.excludedLeak) {
                    info += "* EXCLUDED LEAK.\n"
                }
                info += "* " + result.className
                if (heapDump.referenceName != "") {
                    info += " (" + heapDump.referenceName + ")"
                }
                info += """ has leaked:
${result.leakTrace.toString()}
"""
                if (result.retainedHeapSize != AnalysisResult.RETAINED_HEAP_SKIPPED) {
                    info += """
                        * Retaining: ${Formatter.formatShortFileSize(context, result.retainedHeapSize)}.
                        
                        """.trimIndent()
                }
                if (detailed) {
                    detailedString = """
                        
                        * Details:
                        ${result.leakTrace!!.toDetailedString()}
                        """.trimIndent()
                }
            } else if (result.failure != null) {
                // We duplicate the library version & Sha information because bug reports often only contain
                // the stacktrace.
                info += "* FAILURE in " + BuildConfig.LEAKCANARY_LIBRARY_VERSION + " " + BuildConfig.GIT_SHA + ":" + Log.getStackTraceString(
                        result.failure) + "\n"
            } else {
                info += "* NO LEAK FOUND.\n\n"
            }
            if (detailed) {
                detailedString += """
                    * Excluded Refs:
                    ${heapDump.excludedRefs}
                    """.trimIndent()
            }
            info += """* Reference Key: ${heapDump.referenceKey}
* Device: ${Build.MANUFACTURER} ${Build.BRAND} ${Build.MODEL} ${Build.PRODUCT}
* Android Version: ${Build.VERSION.RELEASE} API: ${Build.VERSION.SDK_INT} LeakCanary: ${BuildConfig.LEAKCANARY_LIBRARY_VERSION} ${BuildConfig.GIT_SHA}
* Durations: watch=${heapDump.watchDurationMs}ms, gc=${heapDump.gcDurationMs}ms, heap dump=${heapDump.heapDumpDurationMs}ms, analysis=${result.analysisDurationMs}ms
$detailedString"""
            return info
        }

        /**
         * Whether the current process is the process running the [HeapAnalyzerService], which is
         * a different process than the normal app process.
         */
        fun isInAnalyzerProcess(context: Context): Boolean {
            var isInAnalyzerProcess = LeakCanaryInternals.isInAnalyzerProcess
            // This only needs to be computed once per process.
            if (isInAnalyzerProcess == null) {
                isInAnalyzerProcess = isInServiceProcess(context, HeapAnalyzerService::class.java)
                LeakCanaryInternals.isInAnalyzerProcess = isInAnalyzerProcess
            }
            return isInAnalyzerProcess
        }
    }

    init {
        throw AssertionError()
    }
}
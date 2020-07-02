/*
 * Copyright (C) 2018 Square, Inc.
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
import android.os.Debug
import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import com.squareup.leakcanary.HeapDump
import com.squareup.leakcanary.InstrumentationLeakResults
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * [InstrumentationLeakDetector] can be used to detect memory leaks in instrumentation
 * tests.
 *
 *
 * To use it, you need to:
 *
 *  * Install a custom RefWatcher that will not trigger heapdumps while the tests run.
 *  * Add an instrumentation test listener (a [RunListener]) that will invoke
 * [.detectLeaks]
 *
 *
 * <h3>Installing the instrumentation RefWatcher</h3>
 *
 *
 * For [.detectLeaks] to work correctly, the [RefWatcher] must keep track of
 * references but not trigger any heap dump until this [.detectLeaks] runs, otherwise an
 * analysis in progress might prevent this listener from performing its own analysis.
 *
 *
 * Create and install the [RefWatcher] instance using
 * [.instrumentationRefWatcher] instead of
 * [LeakCanary.install] or [LeakCanary.refWatcher].
 * <pre>`
 * public class InstrumentationExampleApplication extends ExampleApplication {
 * @Override protected void setupLeakCanary() {
 * InstrumentationLeakDetector.instrumentationRefWatcher(this)
 * .buildAndInstall();
 * }
 * }
`</pre> *
 *
 * <h3>Add an intrumentation test listener</h3>
 *
 *
 * LeakCanary provides [FailTestOnLeakRunListener], but you should feel free to implement
 * your own [RunListener] and call [.detectLeaks] directly if you need a more custom
 * behavior (for instance running it only once per test suite, or reporting to a backend).
 *
 *
 * All you need to do is add the following to the defaultConfig of your build.gradle:
 *
 * <pre>`testInstrumentationRunnerArgument "listener", "FailTestOnLeakRunListener"`</pre>
 *
 *
 * Then you can run your instrumentation tests via Gradle as usually, and they will fail when
 * a memory leak is detected:
 *
 * <pre>`./gradlew leakcanary-sample:connectedCheck`</pre>
 *
 *
 * If instead you want to run UI tests via adb, add a *listener* execution argument to
 * your command line for running the UI tests:
 * `-e listener FailTestOnLeakRunListener`. The full command line
 * should look something like this:
 * <pre>`adb shell am instrument \\
 * -w com.android.foo/android.support.test.runner.AndroidJUnitRunner \\
 * -e listener FailTestOnLeakRunListener
`</pre> *
 *
 * <h3>Rationale</h3>
 * Instead of using the [FailTestOnLeakRunListener], one could simply enable LeakCanary in
 * instrumentation tests.
 *
 *
 * This approach would have two disadvantages:
 *
 *  * Heap dumps freeze the VM, and the leak analysis is IO and CPU heavy. This can slow down
 * the test and introduce flakiness
 *  * The leak analysis is asynchronous by default, and happens in a separate process. This means
 * the tests could finish and the process die before the analysis is finished.
 *
 *
 *
 * The approach taken here is to collect all references to watch as you run the test, but not
 * do any heap dump during the test. Then, at the end, if any of the watched objects is still in
 * memory we dump the heap and perform a blocking analysis. There is only one heap dump performed,
 * no matter the number of objects leaking, and then we iterate on the leaking references in the
 * heap dump and provide all result in a [InstrumentationLeakResults].
 */
class InstrumentationLeakDetector {
    fun detectLeaks(): InstrumentationLeakResults {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val refWatcher = LeakCanary.installedRefWatcher()
        val retainedKeys = refWatcher.retainedKeys
        if (refWatcher.isEmpty) {
            return InstrumentationLeakResults.NONE
        }
        instrumentation.waitForIdleSync()
        if (refWatcher.isEmpty) {
            return InstrumentationLeakResults.NONE
        }
        GcTrigger.DEFAULT.runGc()
        if (refWatcher.isEmpty) {
            return InstrumentationLeakResults.NONE
        }

        // Waiting for any delayed UI post (e.g. scroll) to clear. This shouldn't be needed, but
        // Android simply has way too many delayed posts that aren't canceled when views are detached.
        SystemClock.sleep(2000)
        if (refWatcher.isEmpty) {
            return InstrumentationLeakResults.NONE
        }

        // Aaand we wait some more.
        // 4 seconds (2+2) is greater than the 3 seconds delay for
        // FINISH_TOKEN in android.widget.Filter
        SystemClock.sleep(2000)
        GcTrigger.DEFAULT.runGc()
        if (refWatcher.isEmpty) {
            return InstrumentationLeakResults.NONE
        }

        // We're always reusing the same file since we only execute this once at a time.
        val heapDumpFile = File(context.filesDir, "instrumentation_tests_heapdump.hprof")
        try {
            Debug.dumpHprofData(heapDumpFile.absolutePath)
        } catch (e: Exception) {
            CanaryLog.d(e, "Could not dump heap")
            return InstrumentationLeakResults.NONE
        }
        val heapDumpBuilder = refWatcher.heapDumpBuilder
        val heapAnalyzer = HeapAnalyzer(heapDumpBuilder.excludedRefs, AnalyzerProgressListener.NONE,
                heapDumpBuilder.reachabilityInspectorClasses)
        val trackedReferences = heapAnalyzer.findTrackedReferences(heapDumpFile)
        val detectedLeaks: MutableList<InstrumentationLeakResults.Result> = ArrayList()
        val excludedLeaks: MutableList<InstrumentationLeakResults.Result> = ArrayList()
        val failures: MutableList<InstrumentationLeakResults.Result> = ArrayList()
        for (trackedReference in trackedReferences) {
            // Ignore any Weak Reference that this test does not care about.
            if (!retainedKeys.contains(trackedReference.key)) {
                continue
            }
            val heapDump = HeapDump.builder()
                    .heapDumpFile(heapDumpFile)
                    .referenceKey(trackedReference.key)
                    .referenceName(trackedReference.name)
                    .excludedRefs(heapDumpBuilder.excludedRefs)
                    .reachabilityInspectorClasses(heapDumpBuilder.reachabilityInspectorClasses)
                    .build()
            val analysisResult = heapAnalyzer.checkForLeak(heapDumpFile, trackedReference.key, false)
            val leakResult = InstrumentationLeakResults.Result(heapDump, analysisResult)
            if (analysisResult.leakFound) {
                if (!analysisResult.excludedLeak) {
                    detectedLeaks.add(leakResult)
                } else {
                    excludedLeaks.add(leakResult)
                }
            } else if (analysisResult.failure != null) {
                failures.add(leakResult)
            }
        }
        CanaryLog.d("Found %d proper leaks, %d excluded leaks and %d leak analysis failures",
                detectedLeaks.size,
                excludedLeaks.size,
                failures.size)
        return InstrumentationLeakResults(detectedLeaks, excludedLeaks, failures)
    }

    companion object {
        /**
         * Returns a new [] AndroidRefWatcherBuilder that will create a [RefWatcher] suitable
         * for instrumentation tests. This [RefWatcher] will never trigger a heap dump. This should
         * be installed from the test application class, and should be used in combination with a
         * [RunListener] that calls [.detectLeaks], for instance
         * [FailTestOnLeakRunListener].
         */
        fun instrumentationRefWatcher(
                application: Application): AndroidRefWatcherBuilder {
            return LeakCanary.refWatcher(application)
                    .watchExecutor(object : WatchExecutor {
                        // Storing weak refs to ensure they make it to the queue.
                        val trackedReferences: MutableList<Retryable> = CopyOnWriteArrayList()
                        override fun execute(retryable: Retryable) {
                            trackedReferences.add(retryable)
                        }
                    })!!
        }
    }
}
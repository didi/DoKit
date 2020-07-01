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

import android.app.Instrumentation
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

/**
 *
 * A JUnit [RunListener] for detecting memory leaks in Android instrumentation tests. It
 * waits for the end of a test, and if the test succeeds then it will look for leaking
 * references, trigger a heap dump if needed and perform an analysis.
 *
 *  [FailTestOnLeakRunListener] can be subclassed to override
 * [.skipLeakDetectionReason], [.reportLeaks]
 * or [.buildLeakDetectedMessage]
 *
 * @see InstrumentationLeakDetector
 */
open class FailTestOnLeakRunListener : RunListener() {
    private var bundle: Bundle? = null
    private var skipLeakDetectionReason: String? = null
    override fun testStarted(description: Description) {
        skipLeakDetectionReason = skipLeakDetectionReason(description)
        if (skipLeakDetectionReason != null) {
            return
        }
        val testClass = description.className
        val testName = description.methodName
        bundle = Bundle()
        bundle!!.putString(Instrumentation.REPORT_KEY_IDENTIFIER,
                FailTestOnLeakRunListener::class.java.name)
        bundle!!.putString("class", testClass)
        bundle!!.putString("test", testName)
    }

    /**
     * Can be overridden to skip leak detection based on the description provided when a test
     * is started. Returns null to continue leak detection, or a string describing the reason for
     * skipping otherwise.
     */
    protected fun skipLeakDetectionReason(description: Description): String? {
        return null
    }

    override fun testFailure(failure: Failure) {
        skipLeakDetectionReason = "failed"
    }

    override fun testIgnored(description: Description) {
        skipLeakDetectionReason = "was ignored"
    }

    override fun testAssumptionFailure(failure: Failure) {
        skipLeakDetectionReason = "had an assumption failure"
    }

    override fun testFinished(description: Description) {
        detectLeaks()
        LeakCanary.installedRefWatcher().clearWatchedReferences()
    }

    override fun testRunStarted(description: Description) {}
    override fun testRunFinished(result: Result) {}
    private fun detectLeaks() {
        if (skipLeakDetectionReason != null) {
            CanaryLog.d("Skipping leak detection because the test %s", skipLeakDetectionReason.toString())
            skipLeakDetectionReason = null
            return
        }
        val leakDetector = InstrumentationLeakDetector()
        val results = leakDetector.detectLeaks()
        reportLeaks(results)
    }

    /**
     * Can be overridden to report leaks in a different way or do additional reporting.
     */
    protected fun reportLeaks(results: InstrumentationLeakResults) {
        if (!results.detectedLeaks.isEmpty()) {
            val message = Preconditions.checkNotNull(buildLeakDetectedMessage(results.detectedLeaks), "buildLeakDetectedMessage")
            bundle!!.putString("stack", message)
            //public static final int REPORT_VALUE_RESULT_FAILURE = -2;
            InstrumentationRegistry.getInstrumentation().sendStatus(-2, bundle)
        }
    }

    /**
     * Can be overridden to customize the failure string message.
     */
    protected fun buildLeakDetectedMessage(
            detectedLeaks: List<InstrumentationLeakResults.Result>): String {
        val failureMessage = StringBuilder()
        failureMessage.append(
                "Test failed because memory leaks were detected, see leak traces below.\n")
        failureMessage.append(SEPARATOR)
        val context = InstrumentationRegistry.getInstrumentation().context
        for (detectedLeak in detectedLeaks) {
            failureMessage.append(
                    LeakCanary.leakInfo(context, detectedLeak.heapDump, detectedLeak.analysisResult, true))
            failureMessage.append(SEPARATOR)
        }
        return failureMessage.toString()
    }

    companion object {
        private const val SEPARATOR = "######################################\n"
    }
}
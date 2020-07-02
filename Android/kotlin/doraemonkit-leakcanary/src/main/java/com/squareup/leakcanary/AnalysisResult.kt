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

import java.io.Serializable

class AnalysisResult private constructor(
        /** True if a leak was found in the heap dump.  */
        val leakFound: Boolean,
        /**
         * True if [.leakFound] is true and the only path to the leaking reference is
         * through excluded references. Usually, that means you can safely ignore this report.
         */
        val excludedLeak: Boolean,
        /**
         * Class name of the object that leaked, null if [.failure] is not null.
         * The class name format is the same as what would be returned by [Class.getName].
         */
        val className: String?,
        /**
         * Shortest path to GC roots for the leaking object if [.leakFound] is true, null
         * otherwise. This can be used as a unique signature for the leak.
         */
        val leakTrace: LeakTrace?,
        /** Null unless the analysis failed.  */
        val failure: Throwable?,
        /**
         * The number of bytes which would be freed if all references to the leaking object were
         * released. [.RETAINED_HEAP_SKIPPED] if the retained heap size was not computed. 0 if
         * [.leakFound] is false.
         */
        val retainedHeapSize: Long,
        /** Total time spent analyzing the heap.  */
        val analysisDurationMs: Long) : Serializable {

    /**
     *
     * Creates a new [RuntimeException] with a fake stack trace that maps the leak trace.
     *
     *
     * Leak traces uniquely identify memory leaks, much like stack traces uniquely identify
     * exceptions.
     *
     *
     * This method enables you to upload leak traces as stack traces to your preferred
     * exception reporting tool and benefit from the grouping and counting these tools provide out
     * of the box. This also means you can track all leaks instead of relying on individuals
     * reporting them when they happen.
     *
     *
     * The following example leak trace:
     * <pre>
     * * com.foo.WibbleActivity has leaked:
     * * GC ROOT static com.foo.Bar.qux
     * * references com.foo.Quz.context
     * * leaks com.foo.WibbleActivity instance
    </pre> *
     *
     *
     * Will turn into an exception with the following stacktrace:
     * <pre>
     * java.lang.RuntimeException: com.foo.WibbleActivity leak from com.foo.Bar (holder=CLASS,
     * type=STATIC_FIELD)
     * at com.foo.Bar.qux(Bar.java:42)
     * at com.foo.Quz.context(Quz.java:42)
     * at com.foo.WibbleActivity.leaking(WibbleActivity.java:42)
    </pre> *
     */
    fun leakTraceAsFakeException(): RuntimeException {
        if (!leakFound) {
            throw UnsupportedOperationException(
                    "leakTraceAsFakeException() can only be called when leakFound is true")
        }
        val firstElement = leakTrace!!.elements[0]
        val rootSimpleName = classSimpleName(firstElement.className)
        val leakSimpleName = classSimpleName(className)
//        val exceptionMessage = (leakSimpleName
//                + " leak from "
//                + rootSimpleName
//                + " (holder="
//                + firstElement.holder
//                + ", type="
//                + firstElement.type
//                + ")")
        val exceptionMessage =
                "$leakSimpleName leak from $rootSimpleName (holder=${firstElement.holder}, type=${firstElement.type})"
        val exception = RuntimeException(exceptionMessage)
        val stackTrace = arrayOfNulls<StackTraceElement>(leakTrace.elements.size)
        for ((i, element) in leakTrace.elements.withIndex()) {
            val methodName = element.referenceName ?: "leaking"
            val file = classSimpleName(element.className) + ".java"
            stackTrace[i] = StackTraceElement(element.className, methodName, file, 42)
        }
        exception.stackTrace = stackTrace
        return exception
    }

    private fun classSimpleName(className: String?): String? {
        val separator = className!!.lastIndexOf('.')
        return if (separator == -1) className else className.substring(separator + 1)
    }

    companion object {
        const val RETAINED_HEAP_SKIPPED: Long = -1
        @JvmStatic
        fun noLeak(className: String?, analysisDurationMs: Long): AnalysisResult {
            return AnalysisResult(false, false, className, null, null, 0, analysisDurationMs)
        }

        @JvmStatic
        fun leakDetected(excludedLeak: Boolean,
                         className: String,
                         leakTrace: LeakTrace, retainedHeapSize: Long, analysisDurationMs: Long): AnalysisResult {
            return AnalysisResult(true, excludedLeak, className, leakTrace, null, retainedHeapSize,
                    analysisDurationMs)
        }

        @JvmStatic
        fun failure(failure: Throwable,
                    analysisDurationMs: Long): AnalysisResult {
            return AnalysisResult(false, false, null, null, failure, 0, analysisDurationMs)
        }
    }

}
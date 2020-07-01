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

import java.util.*

class InstrumentationLeakResults(detectedLeaks: List<Result>,
                                 excludedLeaks: List<Result>, failures: List<Result>) {
    /** Proper leaks found during instrumentation tests.  */
    @JvmField
    val detectedLeaks: List<Result> = Collections.unmodifiableList(ArrayList(detectedLeaks))

    /**
     * Excluded leaks found during instrumentation tests, based on [ ][RefWatcherBuilder.excludedRefs]
     */
    val excludedLeaks: List<Result> = Collections.unmodifiableList(ArrayList(excludedLeaks))

    /**
     * Leak analysis failures that happened when we tried to detect leaks.
     */
    val failures: List<Result> = Collections.unmodifiableList(ArrayList(failures))

    class Result(val heapDump: HeapDump, val analysisResult: AnalysisResult)

    companion object {
        @JvmField
        val NONE = InstrumentationLeakResults(emptyList(), emptyList(), emptyList())
    }

}
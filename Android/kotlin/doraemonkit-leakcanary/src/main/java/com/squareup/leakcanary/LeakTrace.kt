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

/**
 * A chain of references that constitute the shortest strong reference path from a leaking instance
 * to the GC roots. Fixing the leak usually means breaking one of the references in that chain.
 */
class LeakTrace constructor(val elements: List<LeakTraceElement>, val expectedReachability: List<Reachability>) : Serializable {

    override fun toString(): String = StringBuilder().apply {
        elements.forEachIndexed { index, element ->
            append("* ")
            if (index != 0) {
                append("↳ ")
            }
            val maybeLeakCause = when(expectedReachability[index]) {
                Reachability.UNKNOWN -> true
                Reachability.REACHABLE -> if (index >= elements.size - 1) {
                    expectedReachability[index + 1] != Reachability.REACHABLE
                } else {
                    true
                }
                else -> false
            }

            append(element.toString(maybeLeakCause)).append("\n")
        }
    }.toString()

    fun toDetailedString(): String {
        var string = ""
        for (element in elements) {
            string += element.toDetailedString()
        }
        return string
    }

}
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
package com.squareup.haha.perflib

import kotlin.collections.HashSet

class HahaSpy private constructor() {
    companion object {
        @JvmStatic
        fun allocatingThread(instance: Instance): Instance = instance.mHeap.mSnapshot.let {
            it.findInstance(it.getThread(if (it is RootObj) it.mThread else instance.mStack.mThreadSerialNumber).mId)
        }
//        {
//            val snapshot = instance.mHeap.mSnapshot
//            val threadSerialNumber: Int = if (instance is RootObj) {
//                instance.mThread
//            } else {
//                instance.mStack.mThreadSerialNumber
//            }
//            val thread = snapshot.getThread(threadSerialNumber)
//            return snapshot.findInstance(thread.mId)
//        }

        /**
         * Returns the GC Roots for all heaps in the Snapshot. Unfortunately,
         * [Snapshot.getGCRoots] only returns the GC Roots of the first heap.
         */
        @JvmStatic
        fun allGcRoots(snapshot: Snapshot): Set<RootObj> = HashSet<RootObj>().apply {
            snapshot.heaps.forEach { heap -> this.addAll(heap.mRoots) }
        }
    }

    init {
        throw AssertionError()
    }
}
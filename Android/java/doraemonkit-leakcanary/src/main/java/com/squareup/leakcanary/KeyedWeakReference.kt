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

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/** @see {@link HeapDump.referenceKey}.
 */
class KeyedWeakReference(referent: Any, key: String, name: String,
                                  referenceQueue: ReferenceQueue<Any?>) : WeakReference<Any?>(Preconditions.checkNotNull(referent, "referent"), Preconditions.checkNotNull(referenceQueue, "referenceQueue")) {
    @JvmField
    val key: String = Preconditions.checkNotNull(key, "key")

    @JvmField
    val name: String = Preconditions.checkNotNull(name, "name")

}
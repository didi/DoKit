/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.utils

object ExceptionUtil {
    @JvmStatic
    fun <T : Throwable> propagateIfInstanceOf(t: Throwable, type: Class<T>) {
        if (type.isInstance(t)) {
            throw t
        }
    }

    @JvmStatic
    fun propagate(t: Throwable): RuntimeException {
        propagateIfInstanceOf(t, Error::class.java)
        propagateIfInstanceOf(t, RuntimeException::class.java)
        throw RuntimeException(t)
    }

    fun <T : Throwable> sneakyThrow(t: Throwable) {
        throw t
    }
}
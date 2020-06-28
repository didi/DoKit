/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.utils

import java.nio.charset.Charset

object Utf8Charset {
    const val NAME = "UTF-8"
    val INSTANCE = Charset.forName(NAME)
}
/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.utils;

import java.nio.charset.Charset;

public class Utf8Charset {

  public static final String NAME = "UTF-8";
  public static final Charset INSTANCE = Charset.forName(NAME);
}

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.utils;

public class ExceptionUtil {
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void propagateIfInstanceOf(Throwable t, Class<T> type)
      throws T {
    if (type.isInstance(t)) {
      throw (T)t;
    }
  }

  public static RuntimeException propagate(Throwable t) {
    propagateIfInstanceOf(t, Error.class);
    propagateIfInstanceOf(t, RuntimeException.class);
    throw new RuntimeException(t);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T)t;
  }
}

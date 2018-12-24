/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FilterOutputStream {
  private long mCount;

  public CountingOutputStream(OutputStream out) {
    super(out);
  }

  public long getCount() {
    return mCount;
  }

  @Override
  public void write(int oneByte) throws IOException {
    out.write(oneByte);
    mCount++;
  }

  @Override
  public void write(byte[] buffer) throws IOException {
    write(buffer, 0, buffer.length);
  }

  @Override
  public void write(byte[] buffer, int offset, int length) throws IOException {
    out.write(buffer, offset, length);
    mCount += length;
  }
}

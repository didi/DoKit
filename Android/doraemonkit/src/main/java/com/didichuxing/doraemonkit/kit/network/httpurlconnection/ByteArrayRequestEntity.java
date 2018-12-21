/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayRequestEntity implements SimpleRequestEntity {
  private final byte[] mData;

  public ByteArrayRequestEntity(byte[] data) {
    mData = data;
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {
    out.write(mData);
  }
}

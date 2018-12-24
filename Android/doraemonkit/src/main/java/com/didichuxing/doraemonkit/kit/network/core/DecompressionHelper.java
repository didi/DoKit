/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;

import java.io.IOException;
import java.io.InputStream;

public class DecompressionHelper {
    static final String GZIP_ENCODING = "gzip";
    static final String DEFLATE_ENCODING = "deflate";

    public static InputStream teeInputWithDecompression(
            int requestId,
            InputStream availableInputStream,
            ResponseHandler responseHandler) throws IOException {
        CountingOutputStream decompressedCounter = null;

        return new ResponseHandlingInputStream(
                availableInputStream,
                requestId,
                decompressedCounter,
                responseHandler);
    }
}

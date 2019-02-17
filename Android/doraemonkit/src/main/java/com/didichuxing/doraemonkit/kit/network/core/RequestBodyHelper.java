/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;


import com.didichuxing.doraemonkit.kit.network.stream.GunzippingOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * @desc: 请求body解析器
 */
public class RequestBodyHelper {

    private ByteArrayOutputStream mDeflatedOutput;
    private static final String GZIP_ENCODING = "gzip";
    private static final String DEFLATE_ENCODING = "deflate";

    public OutputStream createBodySink(String contentEncoding) throws IOException {
        OutputStream deflatingOutput;
        ByteArrayOutputStream deflatedOutput = new ByteArrayOutputStream();
        if (GZIP_ENCODING.equals(contentEncoding)) {
            deflatingOutput = GunzippingOutputStream.create(deflatedOutput);
        } else if (DEFLATE_ENCODING.equals(contentEncoding)) {
            deflatingOutput = new InflaterOutputStream(deflatedOutput);
        } else {
            deflatingOutput = deflatedOutput;
        }
        mDeflatedOutput = deflatedOutput;
        return deflatingOutput;
    }

    public byte[] getDisplayBody() {
        throwIfNoBody();
        return mDeflatedOutput.toByteArray();
    }

    public boolean hasBody() {
        return mDeflatedOutput != null;
    }

    private void throwIfNoBody() {
        if (!hasBody()) {
            throw new IllegalStateException("No body found; has createBodySink been called?");
        }
    }
}

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.stream;

import com.didichuxing.doraemonkit.kit.network.core.ResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamProxy extends FilterInputStream {

    public static final String TAG = "ResponseHandlingInputStream";

    private static final int BUFFER_SIZE = 1024;

    private final ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
    private final ResponseHandler mResponseHandler;

    private boolean mClosed;

    private byte[] mSkipBuffer;

    /**
     * @param inputStream
     * @param responseHandler Special interface to intercept read events before they are sent
     *                        to peers via  methods.
     */
    public InputStreamProxy(
            InputStream inputStream,
            ResponseHandler responseHandler) {
        super(inputStream);
        mResponseHandler = responseHandler;
        mClosed = false;
    }

    private synchronized int checkEOF(int n) {
        if (n == -1) {
            if (mResponseHandler != null) {
                mResponseHandler.onEOF(mOutputStream);
            }
            closeOutputStreamQuietly();
        }
        return n;
    }

    @Override
    public int read() throws IOException {
        try {
            int result = checkEOF(in.read());
            if (result != -1) {
                writeToOutputStream(result);
            }
            return result;
        } catch (IOException ex) {
            throw handleIOException(ex);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            int result = checkEOF(in.read(b, off, len));
            if (result != -1) {
                writeToOutputStream(b, off, result);
            }
            return result;
        } catch (IOException ex) {
            throw handleIOException(ex);
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        byte[] buffer = getSkipBufferLocked();
        long total = 0;
        while (total < n) {
            long bytesDiff = n - total;
            int bytesToRead = (int) Math.min((long) buffer.length, bytesDiff);
            int result = this.read(buffer, 0, bytesToRead);
            if (result == -1) {
                break;
            }
            total += result;
        }
        return total;
    }

    private byte[] getSkipBufferLocked() {
        if (mSkipBuffer == null) {
            mSkipBuffer = new byte[BUFFER_SIZE];
        }

        return mSkipBuffer;
    }

    @Override
    public boolean markSupported() {
        // this can be implemented, but isn't needed for TeedInputStream's behavior
        return false;
    }

    @Override
    public void mark(int readlimit) {
        // noop -- mark is not supported
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException("Mark not supported");
    }

    @Override
    public void close() throws IOException {
        super.close();
        closeOutputStreamQuietly();
    }

    private synchronized void closeOutputStreamQuietly() {
        if (!mClosed) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
            } finally {
                mClosed = true;
            }
        }
    }

    private IOException handleIOException(IOException ex) {
        if (mResponseHandler != null) {
            mResponseHandler.onError(ex);
        }
        return ex;
    }

    private synchronized void writeToOutputStream(int oneByte) {
        if (mClosed) {
            return;
        }
        mOutputStream.write(oneByte);
    }

    private synchronized void writeToOutputStream(byte[] b, int offset, int count) {
        if (mClosed) {
            return;
        }

        mOutputStream.write(b, offset, count);
    }

}

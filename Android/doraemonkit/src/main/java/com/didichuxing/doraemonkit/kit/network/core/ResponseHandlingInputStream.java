/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ResponseHandlingInputStream extends FilterInputStream {

    public static final String TAG = "ResponseHandlingInputStream";

    private static final int BUFFER_SIZE = 1024;

    private final int mRequestId;
    private final ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
    private final CountingOutputStream mDecompressedCounter;
    private final ResponseHandler mResponseHandler;

    private boolean mClosed;

    private boolean mEofSeen;

    private byte[] mSkipBuffer;

    private long mLastDecompressedCount = 0;

    /**
     * @param inputStream
     * @param requestId           the requestId to use when we call the
     * @param decompressedCounter Optional decompressing counting output stream which
     *                            can be queried after each write to determine the number of decompressed bytes
     *                            yielded.  Used to implement {@link ResponseHandler#onReadDecoded(int)}.
     * @param responseHandler     Special interface to intercept read events before they are sent
     *                            to peers via  methods.
     */
    public ResponseHandlingInputStream(
            InputStream inputStream,
            int requestId,
            CountingOutputStream decompressedCounter,
            ResponseHandler responseHandler) {
        super(inputStream);
        mRequestId = requestId;
        mDecompressedCounter = decompressedCounter;
        mResponseHandler = responseHandler;
        mClosed = false;
    }

    private synchronized int checkEOF(int n) {
        if (n == -1) {
            mResponseHandler.onEOF(mOutputStream);
            closeOutputStreamQuietly();
            mEofSeen = true;
        }
        return n;
    }

    @Override
    public int read() throws IOException {
        try {
            int result = checkEOF(in.read());
            if (result != -1) {
                mResponseHandler.onRead(1);
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
                mResponseHandler.onRead(result);
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
        try {
            long bytesRead = 0;
            if (!mEofSeen) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = this.read(buffer)) != -1) {
                    bytesRead += count;
                }
            }
            if (bytesRead > 0) {
//        CLog.writeToConsole(
//            mNetworkPeerManager,
//            Console.MessageLevel.ERROR,
//            Console.MessageSource.NETWORK,
//            "There were " + String.valueOf(bytesRead) + " bytes that were not consumed while "
//            + "processing request " + mRequestId);
            }
        } finally {
            super.close();
            closeOutputStreamQuietly();
        }
    }

    private synchronized void closeOutputStreamQuietly() {
        if (!mClosed) {
            try {
                mOutputStream.close();
                reportDecodedSizeIfApplicable();
            } catch (IOException e) {
            } finally {
                mClosed = true;
            }
        }
    }

    private IOException handleIOException(IOException ex) {
        mResponseHandler.onError(ex);
        return ex;
    }

    private void reportDecodedSizeIfApplicable() {
        if (mDecompressedCounter != null) {
            long currentCount = mDecompressedCounter.getCount();
            int delta = (int) (currentCount - mLastDecompressedCount);
            mResponseHandler.onReadDecoded(delta);
            mLastDecompressedCount = currentCount;
        }
    }

    private synchronized void writeToOutputStream(int oneByte) {
        if (mClosed) {
            return;
        }
        mOutputStream.write(oneByte);
        reportDecodedSizeIfApplicable();
    }

    private synchronized void writeToOutputStream(byte[] b, int offset, int count) {
        if (mClosed) {
            return;
        }

        mOutputStream.write(b, offset, count);
        reportDecodedSizeIfApplicable();
    }

}

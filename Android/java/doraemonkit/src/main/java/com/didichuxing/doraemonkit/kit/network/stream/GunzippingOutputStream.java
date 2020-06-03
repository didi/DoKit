/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.stream;

import com.didichuxing.doraemonkit.kit.network.utils.ExceptionUtil;
import com.didichuxing.doraemonkit.kit.network.utils.StreamUtil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;


public class GunzippingOutputStream extends FilterOutputStream {
  private final Future<Void> mCopyFuture;

  private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

  public static GunzippingOutputStream create(OutputStream finalOut) throws IOException {
    PipedInputStream pipeIn = new PipedInputStream();
    PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);

    Future<Void> copyFuture = sExecutor.submit(
        new GunzippingCallable(pipeIn, finalOut));

    return new GunzippingOutputStream(pipeOut, copyFuture);
  }

  private GunzippingOutputStream(OutputStream out, Future<Void> copyFuture) throws IOException {
    super(out);
    mCopyFuture = copyFuture;
  }

  @Override
  public void close() throws IOException {
    boolean success = false;
    try {
      super.close();
      success = true;
    } finally {
      try {
        getAndRethrow(mCopyFuture);
      } catch (IOException e) {
        if (success) {
          throw e;
        }
      }
    }
  }

  private static <T> T getAndRethrow(Future<T> future) throws IOException {
    while (true) {
      try {
        return future.get();
      } catch (InterruptedException e) {
        // Continue...
      } catch (ExecutionException e) {
        Throwable cause = e.getCause();
        ExceptionUtil.propagateIfInstanceOf(cause, IOException.class);
        ExceptionUtil.propagate(cause);
      }
    }
  }

  private static class GunzippingCallable implements Callable<Void> {
    private final InputStream mIn;
    private final OutputStream mOut;

    public GunzippingCallable(InputStream in, OutputStream out) {
      mIn = in;
      mOut = out;
    }

    @Override
    public Void call() throws IOException {
      GZIPInputStream in = new GZIPInputStream(mIn);
      try {
        StreamUtil.copy(in, mOut, new byte[1024]);
      } finally {
        in.close();
        mOut.close();
      }
      return null;
    }
  }
}

/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.NetworkInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.didichuxing.doraemonkit.picasso.MemoryPolicy.shouldReadFromMemoryCache;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom.MEMORY;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.Priority;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.Priority.LOW;
import static com.didichuxing.doraemonkit.picasso.Utils.OWNER_HUNTER;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_DECODED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_EXECUTING;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_JOINED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_REMOVED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_TRANSFORMED;
import static com.didichuxing.doraemonkit.picasso.Utils.getLogIdsForHunter;
import static com.didichuxing.doraemonkit.picasso.Utils.log;

class BitmapHunter implements Runnable {
  /**
   * Global lock for bitmap decoding to ensure that we are only are decoding one at a time. Since
   * this will only ever happen in background threads we help avoid excessive memory thrashing as
   * well as potential OOMs. Shamelessly stolen from Volley.
   */
  private static final Object DECODE_LOCK = new Object();

  private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
    @Override protected StringBuilder initialValue() {
      return new StringBuilder(Utils.THREAD_PREFIX);
    }
  };

  private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

  private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
    @Override public boolean canHandleRequest(Request data) {
      return true;
    }

    @Override public Result load(Request request, int networkPolicy) throws IOException {
      throw new IllegalStateException("Unrecognized type of request: " + request);
    }
  };

  final int sequence;
  final DokitPicasso picasso;
  final Dispatcher dispatcher;
  final Cache cache;
  final Stats stats;
  final String key;
  final Request data;
  final int memoryPolicy;
  int networkPolicy;
  final RequestHandler requestHandler;

  Action action;
  List<Action> actions;
  Bitmap result;
  Future<?> future;
  DokitPicasso.LoadedFrom loadedFrom;
  Exception exception;
  int exifRotation; // Determined during decoding of original resource.
  int retryCount;
  Priority priority;

  BitmapHunter(DokitPicasso picasso, Dispatcher dispatcher, Cache cache, Stats stats, Action action,
               RequestHandler requestHandler) {
    this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
    this.picasso = picasso;
    this.dispatcher = dispatcher;
    this.cache = cache;
    this.stats = stats;
    this.action = action;
    this.key = action.getKey();
    this.data = action.getRequest();
    this.priority = action.getPriority();
    this.memoryPolicy = action.getMemoryPolicy();
    this.networkPolicy = action.getNetworkPolicy();
    this.requestHandler = requestHandler;
    this.retryCount = requestHandler.getRetryCount();
  }

  /**
   * Decode a byte stream into a Bitmap. This method will take into account additional information
   * about the supplied request in order to do the decoding efficiently (such as through leveraging
   * {@code inSampleSize}).
   */
  static Bitmap decodeStream(InputStream stream, Request request) throws IOException {
    MarkableInputStream markStream = new MarkableInputStream(stream);
    stream = markStream;

    long mark = markStream.savePosition(65536); // TODO fix this crap.

    final BitmapFactory.Options options = RequestHandler.createBitmapOptions(request);
    final boolean calculateSize = RequestHandler.requiresInSampleSize(options);

    boolean isWebPFile = Utils.isWebPFile(stream);
    markStream.reset(mark);
    // When decode WebP network stream, BitmapFactory throw JNI Exception and make app crash.
    // Decode byte array instead
    if (isWebPFile) {
      byte[] bytes = Utils.toByteArray(stream);
      if (calculateSize) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
            request);
      }
      return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    } else {
      if (calculateSize) {
        BitmapFactory.decodeStream(stream, null, options);
        RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
            request);

        markStream.reset(mark);
      }
      Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
      if (bitmap == null) {
        // Treat null as an IO exception, we will eventually retry.
        throw new IOException("Failed to decode stream.");
      }
      return bitmap;
    }
  }

  @Override public void run() {
    try {
      updateThreadName(data);

      if (picasso.loggingEnabled) {
        log(OWNER_HUNTER, VERB_EXECUTING, getLogIdsForHunter(this));
      }

      result = hunt();

      if (result == null) {
        dispatcher.dispatchFailed(this);
      } else {
        dispatcher.dispatchComplete(this);
      }
    } catch (Downloader.ResponseException e) {
      if (!e.localCacheOnly || e.responseCode != 504) {
        exception = e;
      }
      dispatcher.dispatchFailed(this);
    } catch (NetworkRequestHandler.ContentLengthException e) {
      exception = e;
      dispatcher.dispatchRetry(this);
    } catch (IOException e) {
      exception = e;
      dispatcher.dispatchRetry(this);
    } catch (OutOfMemoryError e) {
      StringWriter writer = new StringWriter();
      stats.createSnapshot().dump(new PrintWriter(writer));
      exception = new RuntimeException(writer.toString(), e);
      dispatcher.dispatchFailed(this);
    } catch (Exception e) {
      exception = e;
      dispatcher.dispatchFailed(this);
    } finally {
      Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
    }
  }

  Bitmap hunt() throws IOException {
    Bitmap bitmap = null;

    if (shouldReadFromMemoryCache(memoryPolicy)) {
      bitmap = cache.get(key);
      if (bitmap != null) {
        stats.dispatchCacheHit();
        loadedFrom = MEMORY;
        if (picasso.loggingEnabled) {
          log(OWNER_HUNTER, VERB_DECODED, data.logId(), "from cache");
        }
        return bitmap;
      }
    }

    data.networkPolicy = retryCount == 0 ? NetworkPolicy.OFFLINE.index : networkPolicy;
    RequestHandler.Result result = requestHandler.load(data, networkPolicy);
    if (result != null) {
      loadedFrom = result.getLoadedFrom();
      exifRotation = result.getExifOrientation();

      bitmap = result.getBitmap();

      // If there was no Bitmap then we need to decode it from the stream.
      if (bitmap == null) {
        InputStream is = result.getStream();
        try {
          bitmap = decodeStream(is, data);
        } finally {
          Utils.closeQuietly(is);
        }
      }
    }

    if (bitmap != null) {
      if (picasso.loggingEnabled) {
        log(OWNER_HUNTER, VERB_DECODED, data.logId());
      }
      stats.dispatchBitmapDecoded(bitmap);
      if (data.needsTransformation() || exifRotation != 0) {
        synchronized (DECODE_LOCK) {
          if (data.needsMatrixTransform() || exifRotation != 0) {
            bitmap = transformResult(data, bitmap, exifRotation);
            if (picasso.loggingEnabled) {
              log(OWNER_HUNTER, VERB_TRANSFORMED, data.logId());
            }
          }
          if (data.hasCustomTransformations()) {
            bitmap = applyCustomTransformations(data.transformations, bitmap);
            if (picasso.loggingEnabled) {
              log(OWNER_HUNTER, VERB_TRANSFORMED, data.logId(), "from custom transformations");
            }
          }
        }
        if (bitmap != null) {
          stats.dispatchBitmapTransformed(bitmap);
        }
      }
    }

    return bitmap;
  }

  void attach(Action action) {
    boolean loggingEnabled = picasso.loggingEnabled;
    Request request = action.request;

    if (this.action == null) {
      this.action = action;
      if (loggingEnabled) {
        if (actions == null || actions.isEmpty()) {
          log(OWNER_HUNTER, VERB_JOINED, request.logId(), "to empty hunter");
        } else {
          log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, "to "));
        }
      }
      return;
    }

    if (actions == null) {
      actions = new ArrayList<Action>(3);
    }

    actions.add(action);

    if (loggingEnabled) {
      log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, "to "));
    }

    Priority actionPriority = action.getPriority();
    if (actionPriority.ordinal() > priority.ordinal()) {
      priority = actionPriority;
    }
  }

  void detach(Action action) {
    boolean detached = false;
    if (this.action == action) {
      this.action = null;
      detached = true;
    } else if (actions != null) {
      detached = actions.remove(action);
    }

    // The action being detached had the highest priority. Update this
    // hunter's priority with the remaining actions.
    if (detached && action.getPriority() == priority) {
      priority = computeNewPriority();
    }

    if (picasso.loggingEnabled) {
      log(OWNER_HUNTER, VERB_REMOVED, action.request.logId(), getLogIdsForHunter(this, "from "));
    }
  }

  private Priority computeNewPriority() {
    Priority newPriority = LOW;

    boolean hasMultiple = actions != null && !actions.isEmpty();
    boolean hasAny = action != null || hasMultiple;

    // Hunter has no requests, low priority.
    if (!hasAny) {
      return newPriority;
    }

    if (action != null) {
      newPriority = action.getPriority();
    }

    if (hasMultiple) {
      //noinspection ForLoopReplaceableByForEach
      for (int i = 0, n = actions.size(); i < n; i++) {
        Priority actionPriority = actions.get(i).getPriority();
        if (actionPriority.ordinal() > newPriority.ordinal()) {
          newPriority = actionPriority;
        }
      }
    }

    return newPriority;
  }

  boolean cancel() {
    return action == null
        && (actions == null || actions.isEmpty())
        && future != null
        && future.cancel(false);
  }

  boolean isCancelled() {
    return future != null && future.isCancelled();
  }

  boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
    boolean hasRetries = retryCount > 0;
    if (!hasRetries) {
      return false;
    }
    retryCount--;
    return requestHandler.shouldRetry(airplaneMode, info);
  }

  boolean supportsReplay() {
    return requestHandler.supportsReplay();
  }

  Bitmap getResult() {
    return result;
  }

  String getKey() {
    return key;
  }

  int getMemoryPolicy() {
    return memoryPolicy;
  }

  Request getData() {
    return data;
  }

  Action getAction() {
    return action;
  }

  DokitPicasso getPicasso() {
    return picasso;
  }

  List<Action> getActions() {
    return actions;
  }

  Exception getException() {
    return exception;
  }

  DokitPicasso.LoadedFrom getLoadedFrom() {
    return loadedFrom;
  }

  Priority getPriority() {
    return priority;
  }

  static void updateThreadName(Request data) {
    String name = data.getName();

    StringBuilder builder = NAME_BUILDER.get();
    builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
    builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);

    Thread.currentThread().setName(builder.toString());
  }

  static BitmapHunter forRequest(DokitPicasso picasso, Dispatcher dispatcher, Cache cache, Stats stats,
                                 Action action) {
    Request request = action.getRequest();
    List<RequestHandler> requestHandlers = picasso.getRequestHandlers();

    // Index-based loop to avoid allocating an iterator.
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, count = requestHandlers.size(); i < count; i++) {
      RequestHandler requestHandler = requestHandlers.get(i);
      if (requestHandler.canHandleRequest(request)) {
        return new BitmapHunter(picasso, dispatcher, cache, stats, action, requestHandler);
      }
    }

    return new BitmapHunter(picasso, dispatcher, cache, stats, action, ERRORING_HANDLER);
  }

  static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result) {
    for (int i = 0, count = transformations.size(); i < count; i++) {
      final Transformation transformation = transformations.get(i);
      Bitmap newResult;
      try {
        newResult = transformation.transform(result);
      } catch (final RuntimeException e) {
        DokitPicasso.HANDLER.post(new Runnable() {
          @Override public void run() {
            throw new RuntimeException(
                "Transformation " + transformation.key() + " crashed with exception.", e);
          }
        });
        return null;
      }

      if (newResult == null) {
        final StringBuilder builder = new StringBuilder() //
            .append("Transformation ")
            .append(transformation.key())
            .append(" returned null after ")
            .append(i)
            .append(" previous transformation(s).\n\nTransformation list:\n");
        for (Transformation t : transformations) {
          builder.append(t.key()).append('\n');
        }
        DokitPicasso.HANDLER.post(new Runnable() {
          @Override public void run() {
            throw new NullPointerException(builder.toString());
          }
        });
        return null;
      }

      if (newResult == result && result.isRecycled()) {
        DokitPicasso.HANDLER.post(new Runnable() {
          @Override public void run() {
            throw new IllegalStateException("Transformation "
                + transformation.key()
                + " returned input Bitmap but recycled it.");
          }
        });
        return null;
      }

      // If the transformation returned a new bitmap ensure they recycled the original.
      if (newResult != result && !result.isRecycled()) {
        DokitPicasso.HANDLER.post(new Runnable() {
          @Override public void run() {
            throw new IllegalStateException("Transformation "
                + transformation.key()
                + " mutated input Bitmap but failed to recycle the original.");
          }
        });
        return null;
      }

      result = newResult;
    }
    return result;
  }

  static Bitmap transformResult(Request data, Bitmap result, int exifRotation) {
    int inWidth = result.getWidth();
    int inHeight = result.getHeight();
    boolean onlyScaleDown = data.onlyScaleDown;

    int drawX = 0;
    int drawY = 0;
    int drawWidth = inWidth;
    int drawHeight = inHeight;

    Matrix matrix = new Matrix();

    if (data.needsMatrixTransform()) {
      int targetWidth = data.targetWidth;
      int targetHeight = data.targetHeight;

      float targetRotation = data.rotationDegrees;
      if (targetRotation != 0) {
        if (data.hasRotationPivot) {
          matrix.setRotate(targetRotation, data.rotationPivotX, data.rotationPivotY);
        } else {
          matrix.setRotate(targetRotation);
        }
      }

      if (data.centerCrop) {
        float widthRatio = targetWidth / (float) inWidth;
        float heightRatio = targetHeight / (float) inHeight;
        float scaleX, scaleY;
        if (widthRatio > heightRatio) {
          int newSize = (int) Math.ceil(inHeight * (heightRatio / widthRatio));
          drawY = (inHeight - newSize) / 2;
          drawHeight = newSize;
          scaleX = widthRatio;
          scaleY = targetHeight / (float) drawHeight;
        } else {
          int newSize = (int) Math.ceil(inWidth * (widthRatio / heightRatio));
          drawX = (inWidth - newSize) / 2;
          drawWidth = newSize;
          scaleX = targetWidth / (float) drawWidth;
          scaleY = heightRatio;
        }
        if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
          matrix.preScale(scaleX, scaleY);
        }
      } else if (data.centerInside) {
        float widthRatio = targetWidth / (float) inWidth;
        float heightRatio = targetHeight / (float) inHeight;
        float scale = widthRatio < heightRatio ? widthRatio : heightRatio;
        if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
          matrix.preScale(scale, scale);
        }
      } else if ((targetWidth != 0 || targetHeight != 0) //
          && (targetWidth != inWidth || targetHeight != inHeight)) {
        // If an explicit target size has been specified and they do not match the results bounds,
        // pre-scale the existing matrix appropriately.
        // Keep aspect ratio if one dimension is set to 0.
        float sx =
            targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
        float sy =
            targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
        if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
          matrix.preScale(sx, sy);
        }
      }
    }

    if (exifRotation != 0) {
      matrix.preRotate(exifRotation);
    }

    Bitmap newResult =
        Bitmap.createBitmap(result, drawX, drawY, drawWidth, drawHeight, matrix, true);
    if (newResult != result) {
      result.recycle();
      result = newResult;
    }

    return result;
  }

  private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight,
      int targetWidth, int targetHeight) {
    return !onlyScaleDown || inWidth > targetWidth || inHeight > targetHeight;
  }
}

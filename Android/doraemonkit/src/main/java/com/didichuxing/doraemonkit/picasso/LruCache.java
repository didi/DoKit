/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.Context;
import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** A memory cache which uses a least-recently used eviction policy. */
public class LruCache implements Cache {
  final LinkedHashMap<String, Bitmap> map;
  private final int maxSize;

  private int size;
  private int putCount;
  private int evictionCount;
  private int hitCount;
  private int missCount;

  /** Create a cache using an appropriate portion of the available RAM as the maximum size. */
  public LruCache(Context context) {
    this(Utils.calculateMemoryCacheSize(context));
  }

  /** Create a cache with a given maximum size in bytes. */
  public LruCache(int maxSize) {
    if (maxSize <= 0) {
      throw new IllegalArgumentException("Max size must be positive.");
    }
    this.maxSize = maxSize;
    this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
  }

  @Override public Bitmap get(String key) {
    if (key == null) {
      throw new NullPointerException("key == null");
    }

    Bitmap mapValue;
    synchronized (this) {
      mapValue = map.get(key);
      if (mapValue != null) {
        hitCount++;
        return mapValue;
      }
      missCount++;
    }

    return null;
  }

  @Override public void set(String key, Bitmap bitmap) {
    if (key == null || bitmap == null) {
      throw new NullPointerException("key == null || bitmap == null");
    }

    Bitmap previous;
    synchronized (this) {
      putCount++;
      size += Utils.getBitmapBytes(bitmap);
      previous = map.put(key, bitmap);
      if (previous != null) {
        size -= Utils.getBitmapBytes(previous);
      }
    }

    trimToSize(maxSize);
  }

  private void trimToSize(int maxSize) {
    while (true) {
      String key;
      Bitmap value;
      synchronized (this) {
        if (size < 0 || (map.isEmpty() && size != 0)) {
          throw new IllegalStateException(
              getClass().getName() + ".sizeOf() is reporting inconsistent results!");
        }

        if (size <= maxSize || map.isEmpty()) {
          break;
        }

        Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
        key = toEvict.getKey();
        value = toEvict.getValue();
        map.remove(key);
        size -= Utils.getBitmapBytes(value);
        evictionCount++;
      }
    }
  }

  /** Clear the cache. */
  public final void evictAll() {
    trimToSize(-1); // -1 will evict 0-sized elements
  }

  @Override public final synchronized int size() {
    return size;
  }

  @Override public final synchronized int maxSize() {
    return maxSize;
  }

  @Override public final synchronized void clear() {
    evictAll();
  }

  @Override public final synchronized void clearKeyUri(String uri) {
    boolean sizeChanged = false;
    int uriLength = uri.length();
    for (Iterator<Map.Entry<String, Bitmap>> i = map.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String, Bitmap> entry = i.next();
      String key = entry.getKey();
      Bitmap value = entry.getValue();
      int newlineIndex = key.indexOf(Utils.KEY_SEPARATOR);
      if (newlineIndex == uriLength && key.substring(0, newlineIndex).equals(uri)) {
        i.remove();
        size -= Utils.getBitmapBytes(value);
        sizeChanged = true;
      }
    }
    if (sizeChanged) {
      trimToSize(maxSize);
    }
  }

  /** Returns the number of times {@link #get} returned a value. */
  public final synchronized int hitCount() {
    return hitCount;
  }

  /** Returns the number of times {@link #get} returned {@code null}. */
  public final synchronized int missCount() {
    return missCount;
  }

  /** Returns the number of times {@link #set(String, Bitmap)} was called. */
  public final synchronized int putCount() {
    return putCount;
  }

  /** Returns the number of values that have been evicted. */
  public final synchronized int evictionCount() {
    return evictionCount;
  }
}

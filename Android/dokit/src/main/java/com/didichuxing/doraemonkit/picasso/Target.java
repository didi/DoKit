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
import android.graphics.drawable.Drawable;

import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom;

/**
 * Represents an arbitrary listener for image loading.
 * <p>
 * Objects implementing this class <strong>must</strong> have a working implementation of
 * {@link Object#equals(Object)} and {@link Object#hashCode()} for proper storage internally.
 * Instances of this interface will also be compared to determine if view recycling is occurring.
 * It is recommended that you add this interface directly on to a custom view type when using in an
 * adapter to ensure correct recycling behavior.
 */
public interface Target {
  /**
   * Callback when an image has been successfully loaded.
   * <p>
   * <strong>Note:</strong> You must not recycle the bitmap.
   */
  void onBitmapLoaded(Bitmap bitmap, LoadedFrom from);

  /**
   * Callback indicating the image could not be successfully loaded.
   * <p>
   * <strong>Note:</strong> The passed {@link Drawable} may be {@code null} if none has been
   * specified via {@link RequestCreator#error(android.graphics.drawable.Drawable)}
   * or {@link RequestCreator#error(int)}.
   */
  void onBitmapFailed(Drawable errorDrawable);

  /**
   * Callback invoked right before your request is submitted.
   * <p>
   * <strong>Note:</strong> The passed {@link Drawable} may be {@code null} if none has been
   * specified via {@link RequestCreator#placeholder(android.graphics.drawable.Drawable)}
   * or {@link RequestCreator#placeholder(int)}.
   */
  void onPrepareLoad(Drawable placeHolderDrawable);
}

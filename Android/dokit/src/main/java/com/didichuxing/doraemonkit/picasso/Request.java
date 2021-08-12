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
import android.net.Uri;

import com.didichuxing.doraemonkit.picasso.DokitPicasso.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.unmodifiableList;

/** Immutable data about an image and the transformations that will be applied to it. */
public final class Request {
  private static final long TOO_LONG_LOG = TimeUnit.SECONDS.toNanos(5);

  /** A unique ID for the request. */
  int id;
  /** The time that the request was first submitted (in nanos). */
  long started;
  /** The {@link NetworkPolicy} to use for this request. */
  int networkPolicy;

  /**
   * The image URI.
   * <p>
   * This is mutually exclusive with {@link #resourceId}.
   */
  public final Uri uri;
  /**
   * The image resource ID.
   * <p>
   * This is mutually exclusive with {@link #uri}.
   */
  public final int resourceId;
  /**
   * Optional stable key for this request to be used instead of the URI or resource ID when
   * caching. Two requests with the same value are considered to be for the same resource.
   */
  public final String stableKey;
  /** List of custom transformations to be applied after the built-in transformations. */
  public final List<com.didichuxing.doraemonkit.picasso.Transformation> transformations;
  /** Target image width for resizing. */
  public final int targetWidth;
  /** Target image height for resizing. */
  public final int targetHeight;
  /**
   * True if the final image should use the 'centerCrop' scale technique.
   * <p>
   * This is mutually exclusive with {@link #centerInside}.
   */
  public final boolean centerCrop;
  /**
   * True if the final image should use the 'centerInside' scale technique.
   * <p>
   * This is mutually exclusive with {@link #centerCrop}.
   */
  public final boolean centerInside;
  public final boolean onlyScaleDown;
  /** Amount to rotate the image in degrees. */
  public final float rotationDegrees;
  /** Rotation pivot on the X axis. */
  public final float rotationPivotX;
  /** Rotation pivot on the Y axis. */
  public final float rotationPivotY;
  /** Whether or not {@link #rotationPivotX} and {@link #rotationPivotY} are set. */
  public final boolean hasRotationPivot;
  /** Target image config for decoding. */
  public final Bitmap.Config config;
  /** The priority of this request. */
  public final Priority priority;

  private Request(Uri uri, int resourceId, String stableKey, List<com.didichuxing.doraemonkit.picasso.Transformation> transformations,
      int targetWidth, int targetHeight, boolean centerCrop, boolean centerInside,
      boolean onlyScaleDown, float rotationDegrees, float rotationPivotX, float rotationPivotY,
      boolean hasRotationPivot, Bitmap.Config config, Priority priority) {
    this.uri = uri;
    this.resourceId = resourceId;
    this.stableKey = stableKey;
    if (transformations == null) {
      this.transformations = null;
    } else {
      this.transformations = unmodifiableList(transformations);
    }
    this.targetWidth = targetWidth;
    this.targetHeight = targetHeight;
    this.centerCrop = centerCrop;
    this.centerInside = centerInside;
    this.onlyScaleDown = onlyScaleDown;
    this.rotationDegrees = rotationDegrees;
    this.rotationPivotX = rotationPivotX;
    this.rotationPivotY = rotationPivotY;
    this.hasRotationPivot = hasRotationPivot;
    this.config = config;
    this.priority = priority;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder("Request{");
    if (resourceId > 0) {
      sb.append(resourceId);
    } else {
      sb.append(uri);
    }
    if (transformations != null && !transformations.isEmpty()) {
      for (com.didichuxing.doraemonkit.picasso.Transformation transformation : transformations) {
        sb.append(' ').append(transformation.key());
      }
    }
    if (stableKey != null) {
      sb.append(" stableKey(").append(stableKey).append(')');
    }
    if (targetWidth > 0) {
      sb.append(" resize(").append(targetWidth).append(',').append(targetHeight).append(')');
    }
    if (centerCrop) {
      sb.append(" centerCrop");
    }
    if (centerInside) {
      sb.append(" centerInside");
    }
    if (rotationDegrees != 0) {
      sb.append(" rotation(").append(rotationDegrees);
      if (hasRotationPivot) {
        sb.append(" @ ").append(rotationPivotX).append(',').append(rotationPivotY);
      }
      sb.append(')');
    }
    if (config != null) {
      sb.append(' ').append(config);
    }
    sb.append('}');

    return sb.toString();
  }

  String logId() {
    long delta = System.nanoTime() - started;
    if (delta > TOO_LONG_LOG) {
      return plainId() + '+' + TimeUnit.NANOSECONDS.toSeconds(delta) + 's';
    }
    return plainId() + '+' + TimeUnit.NANOSECONDS.toMillis(delta) + "ms";
  }

  String plainId() {
    return "[R" + id + ']';
  }

  String getName() {
    if (uri != null) {
      return String.valueOf(uri.getPath());
    }
    return Integer.toHexString(resourceId);
  }

  public boolean hasSize() {
    return targetWidth != 0 || targetHeight != 0;
  }

  boolean needsTransformation() {
    return needsMatrixTransform() || hasCustomTransformations();
  }

  boolean needsMatrixTransform() {
    return hasSize() || rotationDegrees != 0;
  }

  boolean hasCustomTransformations() {
    return transformations != null;
  }

  public Builder buildUpon() {
    return new Builder(this);
  }

  /** Builder for creating {@link Request} instances. */
  public static final class Builder {
    private Uri uri;
    private int resourceId;
    private String stableKey;
    private int targetWidth;
    private int targetHeight;
    private boolean centerCrop;
    private boolean centerInside;
    private boolean onlyScaleDown;
    private float rotationDegrees;
    private float rotationPivotX;
    private float rotationPivotY;
    private boolean hasRotationPivot;
    private List<com.didichuxing.doraemonkit.picasso.Transformation> transformations;
    private Bitmap.Config config;
    private Priority priority;

    /** Start building a request using the specified {@link Uri}. */
    public Builder(Uri uri) {
      setUri(uri);
    }

    /** Start building a request using the specified resource ID. */
    public Builder(int resourceId) {
      setResourceId(resourceId);
    }

    Builder(Uri uri, int resourceId, Bitmap.Config bitmapConfig) {
      this.uri = uri;
      this.resourceId = resourceId;
      this.config = bitmapConfig;
    }

    private Builder(Request request) {
      uri = request.uri;
      resourceId = request.resourceId;
      stableKey = request.stableKey;
      targetWidth = request.targetWidth;
      targetHeight = request.targetHeight;
      centerCrop = request.centerCrop;
      centerInside = request.centerInside;
      rotationDegrees = request.rotationDegrees;
      rotationPivotX = request.rotationPivotX;
      rotationPivotY = request.rotationPivotY;
      hasRotationPivot = request.hasRotationPivot;
      onlyScaleDown = request.onlyScaleDown;
      if (request.transformations != null) {
        transformations = new ArrayList<com.didichuxing.doraemonkit.picasso.Transformation>(request.transformations);
      }
      config = request.config;
      priority = request.priority;
    }

    boolean hasImage() {
      return uri != null || resourceId != 0;
    }

    boolean hasSize() {
      return targetWidth != 0 || targetHeight != 0;
    }

    boolean hasPriority() {
      return priority != null;
    }

    /**
     * Set the target image Uri.
     * <p>
     * This will clear an image resource ID if one is set.
     */
    public Builder setUri(Uri uri) {
      if (uri == null) {
        throw new IllegalArgumentException("Image URI may not be null.");
      }
      this.uri = uri;
      this.resourceId = 0;
      return this;
    }

    /**
     * Set the target image resource ID.
     * <p>
     * This will clear an image Uri if one is set.
     */
    public Builder setResourceId(int resourceId) {
      if (resourceId == 0) {
        throw new IllegalArgumentException("Image resource ID may not be 0.");
      }
      this.resourceId = resourceId;
      this.uri = null;
      return this;
    }

    /**
     * Set the stable key to be used instead of the URI or resource ID when caching.
     * Two requests with the same value are considered to be for the same resource.
     */
    public Builder stableKey(String stableKey) {
      this.stableKey = stableKey;
      return this;
    }

    /**
     * Resize the image to the specified size in pixels.
     * Use 0 as desired dimension to resize keeping aspect ratio.
     */
    public Builder resize(int targetWidth, int targetHeight) {
      if (targetWidth < 0) {
        throw new IllegalArgumentException("Width must be positive number or 0.");
      }
      if (targetHeight < 0) {
        throw new IllegalArgumentException("Height must be positive number or 0.");
      }
      if (targetHeight == 0 && targetWidth == 0) {
        throw new IllegalArgumentException("At least one dimension has to be positive number.");
      }
      this.targetWidth = targetWidth;
      this.targetHeight = targetHeight;
      return this;
    }

    /** Clear the resize transformation, if any. This will also clear center crop/inside if set. */
    public Builder clearResize() {
      targetWidth = 0;
      targetHeight = 0;
      centerCrop = false;
      centerInside = false;
      return this;
    }

    /**
     * Crops an image inside of the bounds specified by {@link #resize(int, int)} rather than
     * distorting the aspect ratio. This cropping technique scales the image so that it fills the
     * requested bounds and then crops the extra.
     */
    public Builder centerCrop() {
      if (centerInside) {
        throw new IllegalStateException("Center crop can not be used after calling centerInside");
      }
      centerCrop = true;
      return this;
    }

    /** Clear the center crop transformation flag, if set. */
    public Builder clearCenterCrop() {
      centerCrop = false;
      return this;
    }

    /**
     * Centers an image inside of the bounds specified by {@link #resize(int, int)}. This scales
     * the image so that both dimensions are equal to or less than the requested bounds.
     */
    public Builder centerInside() {
      if (centerCrop) {
        throw new IllegalStateException("Center inside can not be used after calling centerCrop");
      }
      centerInside = true;
      return this;
    }

    /** Clear the center inside transformation flag, if set. */
    public Builder clearCenterInside() {
      centerInside = false;
      return this;
    }

    /**
     * Only resize an image if the original image size is bigger than the target size
     * specified by {@link #resize(int, int)}.
     */
    public Builder onlyScaleDown() {
      if (targetHeight == 0 && targetWidth == 0) {
        throw new IllegalStateException("onlyScaleDown can not be applied without resize");
      }
      onlyScaleDown = true;
      return this;
    }

    /** Clear the onlyScaleUp flag, if set. **/
    public Builder clearOnlyScaleDown() {
      onlyScaleDown = false;
      return this;
    }

    /** Rotate the image by the specified degrees. */
    public Builder rotate(float degrees) {
      rotationDegrees = degrees;
      return this;
    }

    /** Rotate the image by the specified degrees around a pivot point. */
    public Builder rotate(float degrees, float pivotX, float pivotY) {
      rotationDegrees = degrees;
      rotationPivotX = pivotX;
      rotationPivotY = pivotY;
      hasRotationPivot = true;
      return this;
    }

    /** Clear the rotation transformation, if any. */
    public Builder clearRotation() {
      rotationDegrees = 0;
      rotationPivotX = 0;
      rotationPivotY = 0;
      hasRotationPivot = false;
      return this;
    }

    /** Decode the image using the specified config. */
    public Builder config(Bitmap.Config config) {
      this.config = config;
      return this;
    }

    /** Execute request using the specified priority. */
    public Builder priority(Priority priority) {
      if (priority == null) {
        throw new IllegalArgumentException("Priority invalid.");
      }
      if (this.priority != null) {
        throw new IllegalStateException("Priority already set.");
      }
      this.priority = priority;
      return this;
    }

    /**
     * Add a custom transformation to be applied to the image.
     * <p>
     * Custom transformations will always be run after the built-in transformations.
     */
    public Builder transform(com.didichuxing.doraemonkit.picasso.Transformation transformation) {
      if (transformation == null) {
        throw new IllegalArgumentException("Transformation must not be null.");
      }
      if (transformation.key() == null) {
        throw new IllegalArgumentException("Transformation key must not be null.");
      }
      if (transformations == null) {
        transformations = new ArrayList<com.didichuxing.doraemonkit.picasso.Transformation>(2);
      }
      transformations.add(transformation);
      return this;
    }

    /**
     * Add a list of custom transformations to be applied to the image.
     * <p>
     * Custom transformations will always be run after the built-in transformations.
     */
    public Builder transform(List<? extends Transformation> transformations) {
      if (transformations == null) {
        throw new IllegalArgumentException("Transformation list must not be null.");
      }
      for (int i = 0, size = transformations.size(); i < size; i++) {
        transform(transformations.get(i));
      }
      return this;
    }

    /** Create the immutable {@link Request} object. */
    public Request build() {
      if (centerInside && centerCrop) {
        throw new IllegalStateException("Center crop and center inside can not be used together.");
      }
      if (centerCrop && (targetWidth == 0 && targetHeight == 0)) {
        throw new IllegalStateException(
            "Center crop requires calling resize with positive width and height.");
      }
      if (centerInside && (targetWidth == 0 && targetHeight == 0)) {
        throw new IllegalStateException(
            "Center inside requires calling resize with positive width and height.");
      }
      if (priority == null) {
        priority = Priority.NORMAL;
      }
      return new Request(uri, resourceId, stableKey, transformations, targetWidth, targetHeight,
          centerCrop, centerInside, onlyScaleDown, rotationDegrees, rotationPivotX, rotationPivotY,
          hasRotationPivot, config, priority);
    }
  }
}

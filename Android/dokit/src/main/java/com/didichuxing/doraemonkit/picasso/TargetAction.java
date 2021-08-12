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

final class TargetAction extends Action<Target> {

  TargetAction(DokitPicasso picasso, Target target, Request data, int memoryPolicy, int networkPolicy,
               Drawable errorDrawable, String key, Object tag, int errorResId) {
    super(picasso, target, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag,
        false);
  }

  @Override void complete(Bitmap result, DokitPicasso.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }
    Target target = getTarget();
    if (target != null) {
      target.onBitmapLoaded(result, from);
      if (result.isRecycled()) {
        throw new IllegalStateException("Target callback must not recycle bitmap!");
      }
    }
  }

  @Override void error() {
    Target target = getTarget();
    if (target != null) {
      if (errorResId != 0) {
        target.onBitmapFailed(picasso.context.getResources().getDrawable(errorResId));
      } else {
        target.onBitmapFailed(errorDrawable);
      }
    }
  }
}

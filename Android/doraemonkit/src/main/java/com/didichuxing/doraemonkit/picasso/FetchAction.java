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

class FetchAction extends Action<Object> {

  private final Object target;
  private Callback callback;

  FetchAction(DokitPicasso picasso, Request data, int memoryPolicy, int networkPolicy, Object tag,
              String key, Callback callback) {
    super(picasso, null, data, memoryPolicy, networkPolicy, 0, null, key, tag, false);
    this.target = new Object();
    this.callback = callback;
  }

  @Override void complete(Bitmap result, DokitPicasso.LoadedFrom from) {
    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override void error() {
    if (callback != null) {
      callback.onError();
    }
  }

  @Override void cancel() {
    super.cancel();
    callback = null;
  }

  @Override Object getTarget() {
    return target;
  }
}

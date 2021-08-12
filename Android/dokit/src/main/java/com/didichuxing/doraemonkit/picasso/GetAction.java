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

class GetAction extends Action<Void> {
  GetAction(DokitPicasso picasso, Request data, int memoryPolicy, int networkPolicy, Object tag,
            String key) {
    super(picasso, null, data, memoryPolicy, networkPolicy, 0, null, key, tag, false);
  }

  @Override void complete(Bitmap result, DokitPicasso.LoadedFrom from) {
  }

  @Override public void error() {
  }
}

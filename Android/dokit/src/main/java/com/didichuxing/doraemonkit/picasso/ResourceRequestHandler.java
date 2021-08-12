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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;

import static android.content.ContentResolver.SCHEME_ANDROID_RESOURCE;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom.DISK;

class ResourceRequestHandler extends RequestHandler {
  private final Context context;

  ResourceRequestHandler(Context context) {
    this.context = context;
  }

  @Override public boolean canHandleRequest(Request data) {
    if (data.resourceId != 0) {
      return true;
    }

    return SCHEME_ANDROID_RESOURCE.equals(data.uri.getScheme());
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    Resources res = Utils.getResources(context, request);
    int id = Utils.getResourceId(res, request);
    return new Result(decodeResource(res, id, request), DISK);
  }

  private static Bitmap decodeResource(Resources resources, int id, Request data) {
    final BitmapFactory.Options options = createBitmapOptions(data);
    if (requiresInSampleSize(options)) {
      BitmapFactory.decodeResource(resources, id, options);
      calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
    }
    return BitmapFactory.decodeResource(resources, id, options);
  }
}

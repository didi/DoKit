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

import android.content.ContentResolver;
import android.content.Context;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom.DISK;

class ContentStreamRequestHandler extends RequestHandler {
  final Context context;

  ContentStreamRequestHandler(Context context) {
    this.context = context;
  }

  @Override public boolean canHandleRequest(Request data) {
    return SCHEME_CONTENT.equals(data.uri.getScheme());
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    return new Result(getInputStream(request), DISK);
  }

  InputStream getInputStream(Request request) throws FileNotFoundException {
    ContentResolver contentResolver = context.getContentResolver();
    return contentResolver.openInputStream(request.uri);
  }
}

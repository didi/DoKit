/*
 * Copyright (C) 2014 Square, Inc.
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentUris.parseId;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Video;
import static android.provider.MediaStore.Images.Thumbnails.FULL_SCREEN_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static com.didichuxing.doraemonkit.picasso.MediaStoreRequestHandler.PicassoKind.FULL;
import static com.didichuxing.doraemonkit.picasso.MediaStoreRequestHandler.PicassoKind.MICRO;
import static com.didichuxing.doraemonkit.picasso.MediaStoreRequestHandler.PicassoKind.MINI;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom.DISK;

class MediaStoreRequestHandler extends ContentStreamRequestHandler {
  private static final String[] CONTENT_ORIENTATION = new String[] {
      Images.ImageColumns.ORIENTATION
  };

  MediaStoreRequestHandler(Context context) {
    super(context);
  }

  @Override public boolean canHandleRequest(Request data) {
    final Uri uri = data.uri;
    return (SCHEME_CONTENT.equals(uri.getScheme())
            && MediaStore.AUTHORITY.equals(uri.getAuthority()));
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    ContentResolver contentResolver = context.getContentResolver();
    int exifOrientation = getExifOrientation(contentResolver, request.uri);

    String mimeType = contentResolver.getType(request.uri);
    boolean isVideo = mimeType != null && mimeType.startsWith("video/");

    if (request.hasSize()) {
      PicassoKind picassoKind = getPicassoKind(request.targetWidth, request.targetHeight);
      if (!isVideo && picassoKind == FULL) {
        return new Result(null, getInputStream(request), DISK, exifOrientation);
      }

      long id = parseId(request.uri);

      BitmapFactory.Options options = createBitmapOptions(request);
      options.inJustDecodeBounds = true;

      calculateInSampleSize(request.targetWidth, request.targetHeight, picassoKind.width,
              picassoKind.height, options, request);

      Bitmap bitmap;

      if (isVideo) {
        // Since MediaStore doesn't provide the full screen kind thumbnail, we use the mini kind
        // instead which is the largest thumbnail size can be fetched from MediaStore.
        int kind = (picassoKind == FULL) ? Video.Thumbnails.MINI_KIND : picassoKind.androidKind;
        bitmap = Video.Thumbnails.getThumbnail(contentResolver, id, kind, options);
      } else {
        bitmap =
            Images.Thumbnails.getThumbnail(contentResolver, id, picassoKind.androidKind, options);
      }

      if (bitmap != null) {
        return new Result(bitmap, null, DISK, exifOrientation);
      }
    }

    return new Result(null, getInputStream(request), DISK, exifOrientation);
  }

  static PicassoKind getPicassoKind(int targetWidth, int targetHeight) {
    if (targetWidth <= MICRO.width && targetHeight <= MICRO.height) {
      return MICRO;
    } else if (targetWidth <= MINI.width && targetHeight <= MINI.height) {
      return MINI;
    }
    return FULL;
  }

  static int getExifOrientation(ContentResolver contentResolver, Uri uri) {
    Cursor cursor = null;
    try {
      cursor = contentResolver.query(uri, CONTENT_ORIENTATION, null, null, null);
      if (cursor == null || !cursor.moveToFirst()) {
        return 0;
      }
      return cursor.getInt(0);
    } catch (RuntimeException ignored) {
      // If the orientation column doesn't exist, assume no rotation.
      return 0;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  enum PicassoKind {
    MICRO(MICRO_KIND, 96, 96),
    MINI(MINI_KIND, 512, 384),
    FULL(FULL_SCREEN_KIND, -1, -1);

    final int androidKind;
    final int width;
    final int height;

    PicassoKind(int androidKind, int width, int height) {
      this.androidKind = androidKind;
      this.width = width;
      this.height = height;
    }
  }
}

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

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.ContactsContract;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.provider.ContactsContract.Contacts.openContactPhotoInputStream;
import static com.didichuxing.doraemonkit.picasso.DokitPicasso.LoadedFrom.DISK;

class ContactsPhotoRequestHandler extends RequestHandler {
  /** A lookup uri (e.g. content://com.android.contacts/contacts/lookup/3570i61d948d30808e537) */
  private static final int ID_LOOKUP = 1;
  /** A contact thumbnail uri (e.g. content://com.android.contacts/contacts/38/photo) */
  private static final int ID_THUMBNAIL = 2;
  /** A contact uri (e.g. content://com.android.contacts/contacts/38) */
  private static final int ID_CONTACT = 3;
  /**
   * A contact display photo (high resolution) uri
   * (e.g. content://com.android.contacts/display_photo/5)
   */
  private static final int ID_DISPLAY_PHOTO = 4;

  private static final UriMatcher matcher;

  static {
    matcher = new UriMatcher(UriMatcher.NO_MATCH);
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*/#", ID_LOOKUP);
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*", ID_LOOKUP);
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/#/photo", ID_THUMBNAIL);
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/#", ID_CONTACT);
    matcher.addURI(ContactsContract.AUTHORITY, "display_photo/#", ID_DISPLAY_PHOTO);
  }

  private final Context context;

  ContactsPhotoRequestHandler(Context context) {
    this.context = context;
  }

  @Override public boolean canHandleRequest(Request data) {
    final Uri uri = data.uri;
    return (SCHEME_CONTENT.equals(uri.getScheme())
        && ContactsContract.Contacts.CONTENT_URI.getHost().equals(uri.getHost())
        && matcher.match(data.uri) != UriMatcher.NO_MATCH);
  }

  @Override public Result load(Request request, int networkPolicy) throws IOException {
    InputStream is = getInputStream(request);
    return is != null ? new Result(is, DISK) : null;
  }

  private InputStream getInputStream(Request data) throws IOException {
    ContentResolver contentResolver = context.getContentResolver();
    Uri uri = data.uri;
    switch (matcher.match(uri)) {
      case ID_LOOKUP:
        uri = ContactsContract.Contacts.lookupContact(contentResolver, uri);
        if (uri == null) {
          return null;
        }
        // Resolved the uri to a contact uri, intentionally fall through to process the resolved uri
      case ID_CONTACT:
        if (SDK_INT < ICE_CREAM_SANDWICH) {
          return openContactPhotoInputStream(contentResolver, uri);
        } else {
          return ContactPhotoStreamIcs.get(contentResolver, uri);
        }
      case ID_THUMBNAIL:
      case ID_DISPLAY_PHOTO:
        return contentResolver.openInputStream(uri);
      default:
        throw new IllegalStateException("Invalid uri: " + uri);
    }
  }

  @TargetApi(ICE_CREAM_SANDWICH)
  private static class ContactPhotoStreamIcs {
    static InputStream get(ContentResolver contentResolver, Uri uri) {
      return openContactPhotoInputStream(contentResolver, uri, true);
    }
  }
}

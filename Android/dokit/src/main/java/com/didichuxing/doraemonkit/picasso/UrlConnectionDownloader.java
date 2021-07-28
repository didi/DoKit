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
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.didichuxing.doraemonkit.picasso.Utils.parseResponseSourceHeader;

/**
 * A {@link Downloader} which uses {@link HttpURLConnection} to download images. A disk cache of 2%
 * of the total available space will be used (capped at 50MB) will automatically be installed in the
 * application's cache directory, when available.
 */
public class UrlConnectionDownloader implements Downloader {
  static final String RESPONSE_SOURCE = "X-Android-Response-Source";
  static volatile Object cache;

  private static final Object lock = new Object();
  private static final String FORCE_CACHE = "only-if-cached,max-age=2147483647";
  private static final ThreadLocal<StringBuilder> CACHE_HEADER_BUILDER =
      new ThreadLocal<StringBuilder>() {
        @Override protected StringBuilder initialValue() {
          return new StringBuilder();
        }
      };

  private final Context context;

  public UrlConnectionDownloader(Context context) {
    this.context = context.getApplicationContext();
  }

  protected HttpURLConnection openConnection(Uri path) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(path.toString()).openConnection();
    connection.setConnectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT_MILLIS);
    connection.setReadTimeout(Utils.DEFAULT_READ_TIMEOUT_MILLIS);
    return connection;
  }

  @Override public Response load(Uri uri, int networkPolicy) throws IOException {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      installCacheIfNeeded(context);
    }

    HttpURLConnection connection = openConnection(uri);
    connection.setUseCaches(true);

    if (networkPolicy != 0) {
      String headerValue;

      if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
        headerValue = FORCE_CACHE;
      } else {
        StringBuilder builder = CACHE_HEADER_BUILDER.get();
        builder.setLength(0);

        if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
          builder.append("no-cache");
        }
        if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
          if (builder.length() > 0) {
            builder.append(',');
          }
          builder.append("no-store");
        }

        headerValue = builder.toString();
      }

      connection.setRequestProperty("Cache-Control", headerValue);
    }

    int responseCode = connection.getResponseCode();
    if (responseCode >= 300) {
      connection.disconnect();
      throw new ResponseException(responseCode + " " + connection.getResponseMessage(),
          networkPolicy, responseCode);
    }

    long contentLength = connection.getHeaderFieldInt("Content-Length", -1);
    boolean fromCache = parseResponseSourceHeader(connection.getHeaderField(RESPONSE_SOURCE));

    return new Response(connection.getInputStream(), fromCache, contentLength);
  }

  @Override public void shutdown() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && cache != null) {
      ResponseCacheIcs.close(cache);
    }
  }

  private static void installCacheIfNeeded(Context context) {
    // DCL + volatile should be safe after Java 5.
    if (cache == null) {
      try {
        synchronized (lock) {
          if (cache == null) {
            cache = ResponseCacheIcs.install(context);
          }
        }
      } catch (IOException ignored) {
      }
    }
  }

  private static class ResponseCacheIcs {
    static Object install(Context context) throws IOException {
      File cacheDir = Utils.createDefaultCacheDir(context);
      HttpResponseCache cache = HttpResponseCache.getInstalled();
      if (cache == null) {
        long maxSize = Utils.calculateDiskCacheSize(cacheDir);
        cache = HttpResponseCache.install(cacheDir, maxSize);
      }
      return cache;
    }

    static void close(Object cache) {
      try {
        ((HttpResponseCache) cache).close();
      } catch (IOException ignored) {
      }
    }
  }
}

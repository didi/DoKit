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
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static com.didichuxing.doraemonkit.picasso.DokitPicasso.Priority;

abstract class Action<T> {
  static class RequestWeakReference<M> extends WeakReference<M> {
    final Action action;

    public RequestWeakReference(Action action, M referent, ReferenceQueue<? super M> q) {
      super(referent, q);
      this.action = action;
    }
  }

  final DokitPicasso picasso;
  final Request request;
  final WeakReference<T> target;
  final boolean noFade;
  final int memoryPolicy;
  final int networkPolicy;
  final int errorResId;
  final Drawable errorDrawable;
  final String key;
  final Object tag;

  boolean willReplay;
  boolean cancelled;

  Action(DokitPicasso picasso, T target, Request request, int memoryPolicy, int networkPolicy,
         int errorResId, Drawable errorDrawable, String key, Object tag, boolean noFade) {
    this.picasso = picasso;
    this.request = request;
    this.target =
        target == null ? null : new RequestWeakReference<T>(this, target, picasso.referenceQueue);
    this.memoryPolicy = memoryPolicy;
    this.networkPolicy = networkPolicy;
    this.noFade = noFade;
    this.errorResId = errorResId;
    this.errorDrawable = errorDrawable;
    this.key = key;
    this.tag = (tag != null ? tag : this);
  }

  abstract void complete(Bitmap result, DokitPicasso.LoadedFrom from);

  abstract void error();

  void cancel() {
    cancelled = true;
  }

  Request getRequest() {
    return request;
  }

  T getTarget() {
    return target == null ? null : target.get();
  }

  String getKey() {
    return key;
  }

  boolean isCancelled() {
    return cancelled;
  }

  boolean willReplay() {
    return willReplay;
  }

  int getMemoryPolicy() {
    return memoryPolicy;
  }

  int getNetworkPolicy() {
    return networkPolicy;
  }

  DokitPicasso getPicasso() {
    return picasso;
  }

  Priority getPriority() {
    return request.priority;
  }

  Object getTag() {
    return tag;
  }
}

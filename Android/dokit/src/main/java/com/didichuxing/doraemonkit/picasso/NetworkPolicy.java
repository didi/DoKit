/*
 * Copyright (C) 2015 Square, Inc.
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

/** Designates the policy to use for network requests. */
@SuppressWarnings("PointlessBitwiseExpression")
public enum NetworkPolicy {

  /** Skips checking the disk cache and forces loading through the network. */
  NO_CACHE(1 << 0),

  /**
   * Skips storing the result into the disk cache.
   * <p>
   * <em>Note</em>: At this time this is only supported if you are using OkHttp.
   */
  NO_STORE(1 << 1),

  /** Forces the request through the disk cache only, skipping network. */
  OFFLINE(1 << 2);

  public static boolean shouldReadFromDiskCache(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.NO_CACHE.index) == 0;
  }

  public static boolean shouldWriteToDiskCache(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.NO_STORE.index) == 0;
  }

  public static boolean isOfflineOnly(int networkPolicy) {
    return (networkPolicy & NetworkPolicy.OFFLINE.index) != 0;
  }

  final int index;

  private NetworkPolicy(int index) {
    this.index = index;
  }
}

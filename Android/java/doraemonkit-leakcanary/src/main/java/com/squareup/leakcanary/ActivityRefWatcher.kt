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
package com.squareup.leakcanary

import android.app.Activity
import android.app.Application
import android.content.Context
import com.squareup.leakcanary.internal.ActivityLifecycleCallbacksAdapter

@Deprecated("""This was initially part of the LeakCanary API, but should not be any more.
  {@link AndroidRefWatcherBuilder#watchActivities} should be used instead.
  We will make this class internal in the next major version.""")
class ActivityRefWatcher private constructor(private val application: Application, private val refWatcher: RefWatcher) {

    private val lifecycleCallbacks: Application.ActivityLifecycleCallbacks = object : ActivityLifecycleCallbacksAdapter() {
        override fun onActivityDestroyed(activity: Activity) {
            //当activity被关闭时 进行内存泄漏查找
            refWatcher.watch(activity)
        }
    }

    fun watchActivities() {
        // Make sure you don't get installed twice.
        stopWatchingActivities()
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun stopWatchingActivities() {
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    companion object {
        private const val TAG = "ActivityRefWatcher"
        fun installOnIcsPlus(application: Application,
                             refWatcher: RefWatcher) {
            install(application, refWatcher)
        }

        @JvmStatic
        fun install(context: Context, refWatcher: RefWatcher) {
            val application = context.applicationContext as Application
            val activityRefWatcher = ActivityRefWatcher(application, refWatcher)
            application.registerActivityLifecycleCallbacks(activityRefWatcher.lifecycleCallbacks)
        }
    }

}
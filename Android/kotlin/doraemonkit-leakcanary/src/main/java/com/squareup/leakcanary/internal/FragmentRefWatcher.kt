/*
 * Copyright (C) 2018 Square, Inc.
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
package com.squareup.leakcanary.internal

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import com.squareup.leakcanary.RefWatcher
import java.util.*

/**
 * Internal class used to watch for fragments leaks.
 */
interface FragmentRefWatcher {
    fun watchFragments(activity: Activity?)
    class Helper private constructor(private val fragmentRefWatchers: List<FragmentRefWatcher>) {
        private val activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks = object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
                for (watcher in fragmentRefWatchers) {
                    watcher.watchFragments(activity)
                }
            }
        }

        companion object {
            private const val SUPPORT_FRAGMENT_REF_WATCHER_CLASS_NAME = "SupportFragmentRefWatcher"
            @JvmStatic
            fun install(context: Context, refWatcher: RefWatcher?) {
                val fragmentRefWatchers: MutableList<FragmentRefWatcher> = ArrayList()
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    fragmentRefWatchers.add(AndroidOFragmentRefWatcher(refWatcher!!))
                }
                try {
                    val fragmentRefWatcherClass = Class.forName(SUPPORT_FRAGMENT_REF_WATCHER_CLASS_NAME)
                    val constructor = fragmentRefWatcherClass.getDeclaredConstructor(RefWatcher::class.java)
                    val supportFragmentRefWatcher = constructor.newInstance(refWatcher) as FragmentRefWatcher
                    fragmentRefWatchers.add(supportFragmentRefWatcher)
                } catch (ignored: Exception) {
                }
                if (fragmentRefWatchers.size == 0) {
                    return
                }
                val helper = Helper(fragmentRefWatchers)
                val application = context.applicationContext as Application
                application.registerActivityLifecycleCallbacks(helper.activityLifecycleCallbacks)
            }
        }

    }
}
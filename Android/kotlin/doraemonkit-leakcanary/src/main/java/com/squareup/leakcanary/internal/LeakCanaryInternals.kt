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
package com.squareup.leakcanary.internal

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ServiceInfo
import android.os.AsyncTask
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Process
import com.squareup.leakcanary.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class LeakCanaryInternals private constructor() {
    companion object {
        const val SAMSUNG = "samsung"
        const val MOTOROLA = "motorola"
        const val LENOVO = "LENOVO"
        const val LG = "LGE"
        const val NVIDIA = "NVIDIA"
        const val MEIZU = "Meizu"
        const val HUAWEI = "HUAWEI"
        const val VIVO = "vivo"

        @JvmField
        @Volatile
        var installedRefWatcher: RefWatcher? = null

        @Volatile
        private var leakDirectoryProvider: LeakDirectoryProvider? = null
        private const val NOTIFICATION_CHANNEL_ID = "leakcanary"

        @JvmField
        @Volatile
        var isInAnalyzerProcess: Boolean? = null

        /** Extracts the class simple name out of a string containing a fully qualified class name.  */
        @JvmStatic
        fun classSimpleName(className: String): String {
            val separator = className.lastIndexOf('.')
            return if (separator == -1) {
                className
            } else {
                className.substring(separator + 1)
            }
        }

        @JvmStatic
        fun setEnabledAsync(context: Context, componentClass: Class<*>?,
                            enabled: Boolean) {
            val appContext = context.applicationContext
            AsyncTask.THREAD_POOL_EXECUTOR.execute { setEnabledBlocking(appContext, componentClass, enabled) }
        }

        @JvmStatic
        fun setEnabledBlocking(appContext: Context, componentClass: Class<*>?,
                               enabled: Boolean) {
            val component = ComponentName(appContext, componentClass!!)
            val packageManager = appContext.packageManager
            val newState = if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            // Blocks on IPC.
            packageManager.setComponentEnabledSetting(component, newState, PackageManager.DONT_KILL_APP)
        }

        @JvmStatic
        fun isInServiceProcess(context: Context, serviceClass: Class<out Service?>?): Boolean {
            val packageManager = context.packageManager
            val packageInfo: PackageInfo
            packageInfo = try {
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SERVICES)
            } catch (e: Exception) {
                CanaryLog.d(e, "Could not get package info for %s", context.packageName)
                return false
            }
            val mainProcess = packageInfo.applicationInfo.processName
            val component = ComponentName(context, serviceClass!!)
            val serviceInfo: ServiceInfo
            serviceInfo = try {
                packageManager.getServiceInfo(component, PackageManager.GET_DISABLED_COMPONENTS)
            } catch (ignored: NameNotFoundException) {
                // Service is disabled.
                return false
            }
            if (serviceInfo.processName == mainProcess) {
                CanaryLog.d("Did not expect service %s to run in main process %s", serviceClass, mainProcess)
                // Technically we are in the service process, but we're not in the service dedicated process.
                return false
            }
            val myPid = Process.myPid()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var myProcess: RunningAppProcessInfo? = null
            val runningProcesses: List<RunningAppProcessInfo>?
            runningProcesses = try {
                activityManager.runningAppProcesses
            } catch (exception: SecurityException) {
                // https://github.com/square/leakcanary/issues/948
                CanaryLog.d("Could not get running app processes %d", exception)
                return false
            }
            if (runningProcesses != null) {
                for (process in runningProcesses) {
                    if (process.pid == myPid) {
                        myProcess = process
                        break
                    }
                }
            }
            if (myProcess == null) {
                CanaryLog.d("Could not find running process for %d", myPid)
                return false
            }
            return myProcess.processName == serviceInfo.processName
        }

        @JvmStatic
        fun showNotification(context: Context, contentTitle: CharSequence?,
                             contentText: CharSequence?, pendingIntent: PendingIntent?, notificationId: Int) {
            val builder = Notification.Builder(context)
                    .setContentText(contentText)
                    .setContentTitle(contentTitle)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            val notification = buildNotification(context, builder)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        }

        @JvmStatic
        fun buildNotification(context: Context,
                              builder: Notification.Builder): Notification {
            builder.setSmallIcon(R.mipmap.leak_canary_notification)
                    .setWhen(System.currentTimeMillis())
                    .setOnlyAlertOnce(true)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                var notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
                if (notificationChannel == null) {
                    val channelName = context.getString(R.string.leak_canary_notification_channel)
                    notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,
                            NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
                builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            }
            return if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
                builder.notification
            } else {
                builder.build()
            }
        }

        @JvmStatic
        fun newSingleThreadExecutor(threadName: String): Executor {
            return Executors.newSingleThreadExecutor(LeakCanarySingleThreadFactory(threadName))
        }

        @JvmStatic
        fun setLeakDirectoryProvider(leakDirectoryProvider: LeakDirectoryProvider?) {
            check(Companion.leakDirectoryProvider == null) {
                ("Cannot set the LeakDirectoryProvider after it has already "
                        + "been set. Try setting it before installing the RefWatcher.")
            }
            Companion.leakDirectoryProvider = leakDirectoryProvider
        }

        @JvmStatic
        fun getLeakDirectoryProvider(context: Context?): LeakDirectoryProvider {
            var leakDirectoryProvider = leakDirectoryProvider
            if (leakDirectoryProvider == null) {
                leakDirectoryProvider = DefaultLeakDirectoryProvider(context!!)
            }
            return leakDirectoryProvider
        }
    }

    init {
        throw AssertionError()
    }
}
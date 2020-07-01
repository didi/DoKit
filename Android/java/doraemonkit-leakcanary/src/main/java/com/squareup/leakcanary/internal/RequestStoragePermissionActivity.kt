/*
 * Copyright (C) 2016 Square, Inc.
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

import android.Manifest.permission
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Toast
import com.squareup.leakcanary.R

@TargetApi(VERSION_CODES.M) //
class RequestStoragePermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            if (hasStoragePermission()) {
                finish()
                return
            }
            val permissions = arrayOf(
                    permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, 42)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!hasStoragePermission()) {
            Toast.makeText(application, R.string.leak_canary_permission_not_granted, Toast.LENGTH_LONG)
                    .show()
        }
        finish()
    }

    override fun finish() {
        // Reset the animation to avoid flickering.
        overridePendingTransition(0, 0)
        super.finish()
    }

    private fun hasStoragePermission(): Boolean = checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    companion object {
        @JvmStatic
        fun createPendingIntent(context: Context): PendingIntent {
            LeakCanaryInternals.setEnabledBlocking(context, RequestStoragePermissionActivity::class.java, true)
            val intent = Intent(context, RequestStoragePermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class GPSServiceHooker {

    private static final String TAG = "ServiceController";

    public int scanTime = 0;

    private ServiceHookCallback sHookCallback = new ServiceHookCallback() {
        @Override
        public void invoke(Method method, Object[] args) {
            if ("requestLocationUpdates".equals(method.getName())) {
                if (args != null) {
                    for (Object item : args) {
                        if (item != null && "android.location.LocationRequest".equals(item.getClass().getName())) {
                            scanTime++;
                            Log.d(TAG, "GPSServiceHooker: scanTime++");
                        }
                    }
                }
            }
        }

        @Nullable
        @Override
        public Object intercept(Object receiver, Method method, Object[] args) {
            return null;
        }
    };

    public SystemServiceHooker sHookHelper = new SystemServiceHooker(Context.LOCATION_SERVICE, "android.location.ILocationManager", sHookCallback);

}

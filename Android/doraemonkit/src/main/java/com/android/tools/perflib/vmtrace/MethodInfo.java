/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.tools.perflib.vmtrace;

import android.support.annotation.NonNull;

import java.util.Locale;

public class MethodInfo {
    public final long id;
    public final String className;
    public final String methodName;
    public final String signature;
    public final String srcPath;
    public final int srcLineNumber;

    private MethodProfileData mProfileData;

    private String mFullName;
    private String mShortName;

    public MethodInfo(long id, String className, String methodName, String signature,
                      String srcPath, int srcLineNumber) {
        this.id = id;
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.srcPath = srcPath;
        this.srcLineNumber = srcLineNumber;
    }

    public String getFullName() {
        if (mFullName == null) {
            mFullName = String.format(Locale.US, "%s.%s: %s", className, methodName, signature);
        }
        return mFullName;
    }

    public String getShortName() {
        if (mShortName == null) {
            mShortName = String.format(Locale.US, "%s.%s", getUnqualifiedClassName(), methodName);
        }
        return mShortName;
    }

    private String getUnqualifiedClassName() {
        String cn = className;
        int i = cn.lastIndexOf('/');
        if (i > 0) {
            cn = cn.substring(i + 1);
        }
        return cn;
    }

    @NonNull
    public MethodProfileData getProfileData() {
        return mProfileData;
    }

    public void setProfileData(@NonNull MethodProfileData profileData) {
        mProfileData = profileData;
    }

    @Override
    public String toString() {
        return className + "." + methodName + signature;
    }
}

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
import android.support.annotation.Nullable;

public class ThreadInfo {
    /** Thread id */
    private final int mId;

    /** Thread name */
    private final String mName;

    /** Top level call in this thread */
    private final Call mTopLevelCall;

    public ThreadInfo(int threadId, @NonNull String name, @Nullable Call topLevelCall) {
        mId = threadId;
        mName = name;
        mTopLevelCall = topLevelCall;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @Nullable
    public Call getTopLevelCall() {
        return mTopLevelCall;
    }
}

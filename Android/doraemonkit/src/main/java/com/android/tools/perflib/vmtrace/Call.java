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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedInts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Call {
    private final long mMethodId;

    /**
     * Note: The thread entry and exit times are stored as unsigned integers in the trace data.
     * In this model, they are stored as integers, but the getters for all of these time values
     * convert them into longs.
     */
    private final int mEntryGlobalTime;
    private final int mExitGlobalTime;

    private final int mEntryThreadTime;
    private final int mExitThreadTime;

    private final long mInclusiveThreadTimeInCallees;
    private final long mInclusiveGlobalTimeInCallees;

    private final int mDepth;

    /**
     * Indicates whether the current call is recursive. A call is recursive if the same method
     * is present in its backstack.
     */
    private final boolean mIsRecursive;

    private final List<Call> mCallees;

    private Call(@NonNull Builder builder, @NonNull Stack<Long> backStack) {
        mMethodId = builder.mMethodId;

        mEntryThreadTime = builder.mEntryThreadTime;
        mEntryGlobalTime = builder.mEntryGlobalTime;
        mExitThreadTime = builder.mExitThreadTime;
        mExitGlobalTime = builder.mExitGlobalTime;

        mDepth = backStack.size();

        mIsRecursive = backStack.contains(mMethodId);

        if (builder.mCallees == null) {
            mCallees = Collections.emptyList();
        } else {
            backStack.push(mMethodId);
            List<Call> callees = new ArrayList<Call>(builder.mCallees.size());
            for (Builder b : builder.mCallees) {
                callees.add(b.build(backStack));
            }
            backStack.pop();
            mCallees = new ImmutableList.Builder<Call>().addAll(callees).build();
        }

        mInclusiveThreadTimeInCallees = sumInclusiveTimes(mCallees, ClockType.THREAD);
        mInclusiveGlobalTimeInCallees = sumInclusiveTimes(mCallees, ClockType.GLOBAL);
    }

    private long sumInclusiveTimes(@NonNull List<Call> callees, ClockType clockType) {
        long sum = 0;
        for (Call c : callees) {
            sum += c.getInclusiveTime(clockType, TimeUnit.MICROSECONDS);
        }
        return sum;
    }

    public long getMethodId() {
        return mMethodId;
    }

    @NonNull
    public List<Call> getCallees() {
        return mCallees;
    }

    public int getDepth() {
        return mDepth;
    }

    /**
     * Returns true if the call is recursive (another call of the same method id is present
     * in its backstack)
     */
    public boolean isRecursive() {
        return mIsRecursive;
    }

    public long getEntryTime(ClockType clockType, TimeUnit units) {
        long entryTime = clockType == ClockType.THREAD ?
                UnsignedInts.toLong(mEntryThreadTime) : UnsignedInts.toLong(mEntryGlobalTime);
        return units.convert(entryTime, VmTraceData.getDefaultTimeUnits());
    }

    public long getExitTime(ClockType clockType, TimeUnit units) {
        long exitTime = clockType == ClockType.THREAD ?
                UnsignedInts.toLong(mExitThreadTime) : UnsignedInts.toLong(mExitGlobalTime);
        return units.convert(exitTime, VmTraceData.getDefaultTimeUnits());
    }

    public long getInclusiveTime(ClockType clockType, TimeUnit units) {
        long inclusiveTime = clockType == ClockType.THREAD ?
                UnsignedInts.toLong(mExitThreadTime - mEntryThreadTime) :
                UnsignedInts.toLong(mExitGlobalTime - mEntryGlobalTime);
        return units.convert(inclusiveTime, VmTraceData.getDefaultTimeUnits());
    }

    public long getExclusiveTime(ClockType clockType, TimeUnit units) {
        long inclusiveTimeInCallees = clockType == ClockType.THREAD ?
                mInclusiveThreadTimeInCallees : mInclusiveGlobalTimeInCallees;
        long exclusiveTime = getInclusiveTime(clockType, VmTraceData.getDefaultTimeUnits()) -
                inclusiveTimeInCallees;
        return units.convert(exclusiveTime, VmTraceData.getDefaultTimeUnits());
    }

    public static class Builder {
        private final long mMethodId;

        private int mEntryThreadTime;
        private int mEntryGlobalTime;
        private int mExitGlobalTime;
        private int mExitThreadTime;

        private List<Builder> mCallees = null;

        public Builder(long methodId) {
            mMethodId = methodId;
        }

        public long getMethodId() {
            return mMethodId;
        }

        public void setMethodEntryTime(int threadTime, int globalTime) {
            mEntryThreadTime = threadTime;
            mEntryGlobalTime = globalTime;
        }

        public void setMethodExitTime(int threadTime, int globalTime) {
            mExitThreadTime = threadTime;
            mExitGlobalTime = globalTime;
        }

        public void addCallee(Builder c) {
            if (mCallees == null) {
                mCallees = new ArrayList<Builder>();
            }
            mCallees.add(c);
        }

        @Nullable
        public List<Builder> getCallees() {
            return mCallees;
        }

        public int getMethodEntryThreadTime() {
            return mEntryThreadTime;
        }

        public int getMethodEntryGlobalTime() {
            return mEntryGlobalTime;
        }

        public int getMethodExitThreadTime() {
            return mExitThreadTime;
        }

        public int getMethodExitGlobalTime() {
            return mExitGlobalTime;
        }

        @NonNull
        public Call build(@NonNull Stack<Long> backStack) {
            return new Call(this, backStack);
        }
    }

    /**
     * Formats this call and all its call hierarchy using the given {@link Formatter} to
     * print the details for each method.
     */
    public String format(Formatter f) {
        StringBuilder sb = new StringBuilder(100);
        printCallHierarchy(sb, f);
        return sb.toString();
    }

    public interface Formatter {
        String format(Call c);
    }

    private static final Formatter METHOD_ID_FORMATTER = new Formatter() {
        @Override
        public String format(Call c) {
            return Long.toString(c.getMethodId());
        }
    };

    @Override
    public String toString() {
        return format(METHOD_ID_FORMATTER);
    }

    private void printCallHierarchy(@NonNull StringBuilder sb, Formatter formatter) {
        sb.append(" -> ");
        sb.append(formatter.format(this));

        List<Call> callees = getCallees();

        int lineStart = sb.lastIndexOf("\n");
        int depth = sb.length() - (lineStart + 1);

        for (int i = 0; i < callees.size(); i++) {
            if (i != 0) {
                sb.append("\n");
                sb.append(Strings.repeat(" ", depth));
            }

            Call callee = callees.get(i);
            callee.printCallHierarchy(sb, formatter);
        }
    }

    @NonNull
    public Iterator<Call> getCallHierarchyIterator() {
        return new CallHierarchyIterator(this);
    }

    /**
     * An iterator for a call hierarchy. The iteration order matches the order in which the calls
     * were invoked.
     */
    private static class CallHierarchyIterator implements Iterator<Call> {
        private final Stack<Call> mCallStack = new Stack<Call>();

        public CallHierarchyIterator(@NonNull Call top) {
            mCallStack.push(top);
        }

        @Override
        public boolean hasNext() {
            return !mCallStack.isEmpty();
        }

        @Override
        public Call next() {
            if (mCallStack.isEmpty()) {
                return null;
            }

            Call top = mCallStack.pop();

            for (int i = top.getCallees().size() - 1; i >= 0; i--) {
                mCallStack.push(top.getCallees().get(i));
            }

            return top;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

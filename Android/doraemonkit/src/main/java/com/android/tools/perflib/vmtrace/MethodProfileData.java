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

import androidx.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** Statistics per method. */
public class MethodProfileData {
    /** {@link TimeUnit} for all time values stored in this model. */
    private static final TimeUnit DATA_TIME_UNITS = TimeUnit.NANOSECONDS;

    /** Stats maintained per thread. Key is thread id. */
    private final Map<Integer, MethodStats> mPerThreadCumulativeStats;

    /** Stats maintained per thread and per callee. */
    private final Table<Integer, Long, MethodStats> mPerThreadStatsByCallee;

    /** Stats maintained per thread and per caller. */
    private final Table<Integer, Long, MethodStats> mPerThreadStatsByCaller;

    /** Indicates whether this method was ever used recursively. */
    private final boolean mIsRecursive;

    private MethodProfileData(Builder b) {
        mPerThreadCumulativeStats = ImmutableMap.copyOf(b.mPerThreadCumulativeStats);
        mPerThreadStatsByCallee = ImmutableTable.copyOf(b.mPerThreadStatsByCallee);
        mPerThreadStatsByCaller = ImmutableTable.copyOf(b.mPerThreadStatsByCaller);
        mIsRecursive = b.mRecursive;
    }

    /** Returns the number of invocations of this method in a given thread. */
    public long getInvocationCount(ThreadInfo thread) {
        MethodStats stats = mPerThreadCumulativeStats.get(thread.getId());
        return getInvocationCount(stats);
    }

    public long getInvocationCountFromCaller(ThreadInfo thread, Long callerId) {
        MethodStats stats = mPerThreadStatsByCaller.get(thread.getId(), callerId);
        return getInvocationCount(stats);
    }

    /** Returns whether this method was ever called recursively. */
    public boolean isRecursive() {
        return mIsRecursive;
    }

    /** Returns the exclusive time of this method in a particular thread in the given time units. */
    public long getExclusiveTime(ThreadInfo thread, ClockType clockType, TimeUnit unit) {
        MethodStats stats = mPerThreadCumulativeStats.get(thread.getId());
        return getExclusiveTime(stats, clockType, unit);
    }

    /** Returns the inclusive time of this method in a particular thread in the given time units. */
    public long getInclusiveTime(ThreadInfo thread, ClockType clockType, TimeUnit unit) {
        MethodStats stats = mPerThreadCumulativeStats.get(thread.getId());
        return getInclusiveTime(stats, clockType, unit);
    }

    /** Returns the callers for this method in a given thread. (across all its invocations). */
    public Set<Long> getCallers(ThreadInfo thread) {
        Map<Long, MethodStats> perCallerStats = mPerThreadStatsByCaller.row(thread.getId());
        return perCallerStats.keySet();
    }

    /** Returns the callees from this method in a given thread. (across all its invocations). */
    public Set<Long> getCallees(ThreadInfo thread) {
        Map<Long, MethodStats> perCalleeStats = mPerThreadStatsByCallee.row(thread.getId());
        return perCalleeStats.keySet();
    }

    /** Returns the exclusive time of this method when called from the given caller method. */
    public long getExclusiveTimeByCaller(ThreadInfo thread, Long callerId,
            ClockType clockType, TimeUnit unit) {
        MethodStats stats = mPerThreadStatsByCaller.get(thread.getId(), callerId);
        return getExclusiveTime(stats, clockType, unit);
    }

    /** Returns the inclusive time of this method when called from the given caller method. */
    public long getInclusiveTimeByCaller(ThreadInfo thread, Long callerId,
            ClockType clockType, TimeUnit unit) {
        MethodStats stats = mPerThreadStatsByCaller.get(thread.getId(), callerId);
        return getInclusiveTime(stats, clockType, unit);
    }

    /** Returns the inclusive time of the callee when called from this method. */
    public long getInclusiveTimeByCallee(ThreadInfo thread, Long calleeId,
            ClockType clockType, TimeUnit unit) {
        MethodStats stats = mPerThreadStatsByCallee.get(thread.getId(), calleeId);
        return getInclusiveTime(stats, clockType, unit);
    }

    private long getExclusiveTime(@Nullable MethodStats stats, ClockType clockType, TimeUnit unit) {
        return stats != null ? stats.getExclusiveTime(clockType, unit) : 0;
    }

    private long getInclusiveTime(@Nullable MethodStats stats, ClockType clockType,
            TimeUnit unit) {
        return stats != null ? stats.getInclusiveTime(clockType, unit) : 0;
    }

    private long getInvocationCount(MethodStats stats) {
        return stats != null ? stats.getInvocationCount() : 0;
    }

    private static class MethodStats {
        private long mInclusiveThreadTime;
        private long mExclusiveThreadTime;

        private long mInclusiveGlobalTime;
        private long mExclusiveGlobalTime;

        private long mInvocationCount;

        public long getInclusiveTime(ClockType clockType, TimeUnit unit) {
            long time = clockType == ClockType.THREAD ? mInclusiveThreadTime : mInclusiveGlobalTime;
            return unit.convert(time, DATA_TIME_UNITS);
        }

        public long getExclusiveTime(ClockType clockType, TimeUnit unit) {
            long time = clockType == ClockType.THREAD ? mExclusiveThreadTime : mExclusiveGlobalTime;
            return unit.convert(time, DATA_TIME_UNITS);
        }

        private long getInvocationCount() {
            return mInvocationCount;
        }
    }

    public static class Builder {
        private final Map<Integer, MethodStats> mPerThreadCumulativeStats = Maps.newHashMap();
        private final Table<Integer, Long, MethodStats> mPerThreadStatsByCaller =
                HashBasedTable.create();
        private final Table<Integer, Long, MethodStats> mPerThreadStatsByCallee =
                HashBasedTable.create();

        private boolean mRecursive;

        public void addCallTime(Call call, Call parent, ThreadInfo thread) {
            for (ClockType type: ClockType.values()) {
                addExclusiveTime(call, parent, thread, type);

                if (!call.isRecursive()) {
                    addInclusiveTime(call, parent, thread, type);
                }
            }
        }

        private void addExclusiveTime(Call call, Call parent, ThreadInfo thread, ClockType type) {
            long time = call.getExclusiveTime(type, DATA_TIME_UNITS);

            addExclusiveTime(getPerThreadStats(thread), time, type);
            if (parent != null) {
                addExclusiveTime(getPerCallerStats(thread, parent), time, type);
            }
        }

        private void addInclusiveTime(Call call, Call parent, ThreadInfo thread, ClockType type) {
            long time = call.getInclusiveTime(type, DATA_TIME_UNITS);

            addInclusiveTime(getPerThreadStats(thread), time, type);
            if (parent != null) {
                addInclusiveTime(getPerCallerStats(thread, parent), time, type);
            }
            for (Call callee: call.getCallees()) {
                addInclusiveTime(getPerCalleeStats(thread, callee),
                        callee.getInclusiveTime(type, DATA_TIME_UNITS), type);
            }
        }

        private void addInclusiveTime(MethodStats stats, long time, ClockType type) {
            if (type == ClockType.THREAD) {
                stats.mInclusiveThreadTime += time;
            } else {
                stats.mInclusiveGlobalTime += time;
            }
        }

        private void addExclusiveTime(MethodStats stats, long time, ClockType type) {
            if (type == ClockType.THREAD) {
                stats.mExclusiveThreadTime += time;
            } else {
                stats.mExclusiveGlobalTime += time;
            }
        }

        private MethodStats getPerThreadStats(ThreadInfo thread) {
            MethodStats stats = mPerThreadCumulativeStats.get(thread.getId());
            if (stats == null) {
                stats = new MethodStats();
                mPerThreadCumulativeStats.put(thread.getId(), stats);
            }
            return stats;
        }

        private MethodStats getPerCallerStats(ThreadInfo thread, Call parent) {
            return getMethodStatsFromTable(thread.getId(), parent.getMethodId(),
                    mPerThreadStatsByCaller);
        }

        private MethodStats getPerCalleeStats(ThreadInfo thread, Call callee) {
            return getMethodStatsFromTable(thread.getId(), callee.getMethodId(),
                    mPerThreadStatsByCallee);
        }

        private MethodStats getMethodStatsFromTable(Integer threadId, Long methodId,
                Table<Integer, Long, MethodStats> statsTable) {
            MethodStats stats = statsTable.get(threadId, methodId);
            if (stats == null) {
                stats = new MethodStats();
                statsTable.put(threadId, methodId, stats);
            }
            return stats;
        }

        public void incrementInvocationCount(Call c, Call parent, ThreadInfo thread) {
            getPerThreadStats(thread).mInvocationCount++;
            if (parent != null) {
                getPerCallerStats(thread, parent).mInvocationCount++;
            }
            for (Call callee: c.getCallees()) {
                getPerCalleeStats(thread, callee).mInvocationCount++;
            }
        }

        public MethodProfileData build() {
            return new MethodProfileData(this);
        }

        public void setRecursive() {
            mRecursive = true;
        }
    }
}

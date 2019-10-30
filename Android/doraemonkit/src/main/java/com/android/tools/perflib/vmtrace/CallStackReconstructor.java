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

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * {@link CallStackReconstructor} helps in reconstructing per thread call stacks from a sequence of
 * trace events (method entry/exit events).
 */
public class CallStackReconstructor {
    /** Method id corresponding to the top level call under which all calls are nested. */
    private final long mTopLevelCallId;

    /** List of calls currently assumed to be at stack depth 0 (called from the top level) */
    private final List<Call.Builder> mTopLevelCalls = new ArrayList<Call.Builder>();

    /** Current call stack based on the sequence of received trace events. */
    private final Stack<Call.Builder> mCallStack = new Stack<Call.Builder>();

    /** The single top level call under which the entire reconstructed call stack nests. */
    private Call mTopLevelCall;

    /**
     * Constructs a call stack reconstructor with the method id under which
     * the entire call stack should nest.
     * */
    public CallStackReconstructor(long topLevelCallId) {
        mTopLevelCallId = topLevelCallId;
    }

    public void addTraceAction(long methodId, TraceAction action, int threadTime, int globalTime) {
        if (action == TraceAction.METHOD_ENTER) {
            enterMethod(methodId, threadTime, globalTime);
        } else {
            exitMethod(methodId, threadTime, globalTime);
        }
    }

    private void enterMethod(long methodId, int threadTime, int globalTime) {
        Call.Builder cb = new Call.Builder(methodId);
        cb.setMethodEntryTime(threadTime, globalTime);

        if (mCallStack.isEmpty()) {
            mTopLevelCalls.add(cb);
        } else {
            Call.Builder caller = mCallStack.peek();
            caller.addCallee(cb);
        }

        mCallStack.push(cb);
    }

    private void exitMethod(long methodId, int threadTime, int globalTime) {
        if (!mCallStack.isEmpty()) {
            Call.Builder c = mCallStack.pop();
            if (c.getMethodId() != methodId) {
                String msg = String
                        .format("Error during call stack reconstruction. Attempt to exit from method 0x%1$x while in method 0x%2$x",
                                c.getMethodId(), methodId);
                throw new RuntimeException(msg);
            }

            c.setMethodExitTime(threadTime, globalTime);
        } else {
            // We are exiting out of a method that was entered into before tracing was started.
            // In such a case, create this method
            Call.Builder c = new Call.Builder(methodId);

            // All the previous calls at the top level are now assumed to have been called from
            // this method. So mark this method as having called all of those methods, and reset
            // the top level to only include this method
            for (Call.Builder cb : mTopLevelCalls) {
                c.addCallee(cb);
            }
            mTopLevelCalls.clear();
            mTopLevelCalls.add(c);

            c.setMethodExitTime(threadTime, globalTime);

            // We don't know this method's entry times, so we try to guess:
            // If it has atleast 1 callee, then we know it must've been atleast before that callee's
            // start time. If there are no callees, then we just assume that it was just before its
            // exit times.
            int entryThreadTime = threadTime - 1;
            int entryGlobalTime = globalTime - 1;

            if (c.getCallees() != null && !c.getCallees().isEmpty()) {
                Call.Builder callee = c.getCallees().get(0);
                entryThreadTime = Math.max(callee.getMethodEntryThreadTime() - 1, 0);
                entryGlobalTime = Math.max(callee.getMethodEntryGlobalTime() - 1, 0);
            }
            c.setMethodEntryTime(entryThreadTime, entryGlobalTime);
        }
    }

    /**
     * Generates a trace action equivalent to exiting from the given method
     * @param methoId id of the method from which we are exiting
     * @param entryThreadTime method's thread entry time
     * @param entryGlobalTime method's global entry time
     * @param callees from the method that we are exiting
     */
    private void exitMethod(long methoId, int entryThreadTime, int entryGlobalTime,
            @Nullable List<Call.Builder> callees) {
        int lastExitThreadTime;
        int lastExitGlobalTime;

        if (callees == null || callees.isEmpty()) {
            // if the call doesn't have any callees, we assume that it just ran for 1 unit of time
            lastExitThreadTime = entryThreadTime + 1;
            lastExitGlobalTime = entryGlobalTime + 1;
        } else {
            // if it did call other methods, we assume that this call exited 1 unit of time after
            // its last callee exited
            Call.Builder last = callees.get(callees.size() - 1);
            lastExitThreadTime = last.getMethodExitThreadTime() + 1;
            lastExitGlobalTime = last.getMethodExitGlobalTime() + 1;
        }

        exitMethod(methoId, lastExitThreadTime, lastExitGlobalTime);
    }

    private void fixupCallStacks() {
        if (mTopLevelCall != null) {
            return;
        }

        // If there are any methods still on the call stack, then the trace doesn't have
        // exit trace action for them, so clean those up
        while (!mCallStack.isEmpty()) {
            Call.Builder cb = mCallStack.peek();
            exitMethod(cb.getMethodId(), cb.getMethodEntryThreadTime(),
                    cb.getMethodEntryGlobalTime(), cb.getCallees());
        }

        // Now that we have parsed the entire call stack, let us move all of it under a single
        // top level call.
        exitMethod(mTopLevelCallId, 0, 0, mTopLevelCalls);

        // TODO: use global / thread times to infer context switches

        // Build calls from their respective builders
        // Now that we've added the top level call, there should be only 1 top level call
        assert mTopLevelCalls.size() == 1;
        mTopLevelCall = mTopLevelCalls.get(0).build(new Stack<Long>());
    }

    public Call getTopLevel() {
        fixupCallStacks();
        return mTopLevelCall;
    }
}

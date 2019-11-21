/*
 * Copyright (C) 2017 The Android Open Source Project
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

/**
 * This interface used by {@link VmTraceParser}. {@link VmTraceParser} parses a trace file and
 * informs the handler about parsing events.
 */
public interface VmTraceHandler {
    /** @param version - version of the trace file. */
    void setVersion(int version);

    /**
     * Sets a parsed property of the trace file. Example keys are: "elapsed-time-usec", "clock",
     * "data-file-overflow", "vm"
     */
    void setProperty(String key, String value);

    /**
     * Receives notification about a thread.
     *
     * @param id - id of the thread.
     * @param name - name of the thread.
     */
    void addThread(int id, String name);

    /**
     * Receives notification about a method.
     *
     * @param id - id of the method.
     * @param info - {@link MethodInfo} that contains information about the method.
     */
    void addMethod(long id, MethodInfo info);

    /**
     * Receives notification about a method event. Example events are: method entered, method
     * exited.
     *
     * @param threadId - id of the thread where this event happened.
     * @param methodId - id of the method where this event happened.
     * @param methodAction - type of the action
     * @param threadTime - thread time when this event happened.
     * @param globalTime - global time when this event
     */
    void addMethodAction(
            int threadId, long methodId, TraceAction methodAction, int threadTime, int globalTime);

    /** @param startTimeUs - tracing start time. */
    void setStartTimeUs(long startTimeUs);
}

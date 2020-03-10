/*
 * Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.kit.blockmonitor.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Information to trace a block.
 */
public class BlockInfo {

    public static final String SEPARATOR = "\r\n";
    private static final String KV = " = ";
    private static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE);

    public static final String NEW_INSTANCE_METHOD = "newInstance: ";
    private static final String KEY_TIME_COST = "time";
    private static final String KEY_THREAD_TIME_COST = "thread-time";
    private static final String KEY_TIME_COST_START = "time-start";
    private static final String KEY_TIME_COST_END = "time-end";
    private static final String KEY_STACK = "stack";


    // Per Block Info fields
    public long timeCost;
    private long threadTimeCost;
    public long time;

    public String timeStart;
    private String timeEnd;
    public ArrayList<String> threadStackEntries = new ArrayList<>();

    private StringBuilder timeSb = new StringBuilder();
    private StringBuilder stackSb = new StringBuilder();
    public String concernStackString;

    public static BlockInfo newInstance() {
        BlockInfo blockInfo = new BlockInfo();
        return blockInfo;
    }

    public BlockInfo flushString() {
        String separator = SEPARATOR;

        timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator);
        timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator);
        timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator);
        timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator);


        if (threadStackEntries != null && !threadStackEntries.isEmpty()) {
            StringBuilder temp = new StringBuilder();
            for (String s : threadStackEntries) {
                temp.append(s);
                temp.append(separator);
            }
            stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator);
        }
        return this;
    }


    public BlockInfo setThreadStackEntries(ArrayList<String> threadStackEntries) {
        this.threadStackEntries = threadStackEntries;
        return this;
    }

    public BlockInfo setMainThreadTimeCost(long realTimeStart, long realTimeEnd, long threadTimeStart, long threadTimeEnd) {
        timeCost = realTimeEnd - realTimeStart;
        threadTimeCost = threadTimeEnd - threadTimeStart;
        timeStart = TIME_FORMATTER.format(realTimeStart);
        timeEnd = TIME_FORMATTER.format(realTimeEnd);
        return this;
    }

    @Override
    public String toString() {
        return timeSb.toString() + "\n" + stackSb.toString();
    }
}

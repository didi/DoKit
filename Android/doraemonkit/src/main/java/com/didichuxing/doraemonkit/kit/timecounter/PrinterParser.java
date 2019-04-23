package com.didichuxing.doraemonkit.kit.timecounter;

import android.text.TextUtils;
import android.util.Printer;

import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * @author: linjizong
 * @date: 2019/2/19
 * @desc:
 */
public class PrinterParser implements Printer {
    private static final String DISPATCHING_TAG = ">>>>> Dispatching";
    private static final String FINISHED_TAG = "<<<<< Finished";
    private static final String HANDLER_NAME = "android.app.ActivityThread$H";

    public static final int LAUNCH_ACTIVITY = 100;
    public static final int PAUSE_ACTIVITY = 101;
    public static final int PAUSE_ACTIVITY_FINISHING = 102;
    public static final int STOP_ACTIVITY_SHOW = 103;
    public static final int STOP_ACTIVITY_HIDE = 104;
    public static final int SHOW_WINDOW = 105;
    public static final int HIDE_WINDOW = 106;
    public static final int RESUME_ACTIVITY = 107;
    public static final int SEND_RESULT = 108;
    public static final int DESTROY_ACTIVITY = 109;
    public static final int BIND_APPLICATION = 110;
    public static final int EXIT_APPLICATION = 111;
    public static final int NEW_INTENT = 112;
    public static final int RECEIVER = 113;
    public static final int CREATE_SERVICE = 114;
    public static final int SERVICE_ARGS = 115;
    public static final int STOP_SERVICE = 116;

    public static final int CONFIGURATION_CHANGED = 118;
    public static final int CLEAN_UP_CONTEXT = 119;
    public static final int GC_WHEN_IDLE = 120;
    public static final int BIND_SERVICE = 121;
    public static final int UNBIND_SERVICE = 122;
    public static final int DUMP_SERVICE = 123;
    public static final int LOW_MEMORY = 124;
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int RELAUNCH_ACTIVITY = 126;
    public static final int PROFILER_CONTROL = 127;
    public static final int CREATE_BACKUP_AGENT = 128;
    public static final int DESTROY_BACKUP_AGENT = 129;
    public static final int SUICIDE = 130;
    public static final int REMOVE_PROVIDER = 131;
    public static final int ENABLE_JIT = 132;
    public static final int DISPATCH_PACKAGE_BROADCAST = 133;
    public static final int SCHEDULE_CRASH = 134;
    public static final int DUMP_HEAP = 135;
    public static final int DUMP_ACTIVITY = 136;
    public static final int SLEEPING = 137;
    public static final int SET_CORE_SETTINGS = 138;
    public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
    public static final int TRIM_MEMORY = 140;
    public static final int DUMP_PROVIDER = 141;
    public static final int UNSTABLE_PROVIDER_DIED = 142;
    public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
    public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
    public static final int INSTALL_PROVIDER = 145;
    public static final int ON_NEW_ACTIVITY_OPTIONS = 146;
    public static final int CANCEL_VISIBLE_BEHIND = 147;
    public static final int BACKGROUND_VISIBLE_BEHIND_CHANGED = 148;
    public static final int ENTER_ANIMATION_COMPLETE = 149;
    public static final int START_BINDER_TRACKING = 150;
    public static final int STOP_BINDER_TRACKING_AND_DUMP = 151;
    public static final int MULTI_WINDOW_MODE_CHANGED = 152;
    public static final int PICTURE_IN_PICTURE_MODE_CHANGED = 153;
    public static final int LOCAL_VOICE_INTERACTION_STARTED = 154;
    public static final int ATTACH_AGENT = 155;
    public static final int APPLICATION_INFO_CHANGED = 156;
    public static final int ACTIVITY_MOVED_TO_DISPLAY = 157;

    private int sCurrentMsg;

    public void parse(String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        if (!log.contains(HANDLER_NAME)) {
            return;
        }
        if (log.startsWith(DISPATCHING_TAG)) {
            if (log.endsWith(String.valueOf(PAUSE_ACTIVITY))) {
                LogHelper.d("PinterParser", "pause");
                TimeCounterManager.get().onActivityStart();
                sCurrentMsg = PAUSE_ACTIVITY;
            } else if (log.endsWith(String.valueOf(LAUNCH_ACTIVITY))) {
                LogHelper.d("PinterParser", "launch");
                TimeCounterManager.get().onActivityLaunch();
                sCurrentMsg = LAUNCH_ACTIVITY;
            }
        } else if (log.startsWith(FINISHED_TAG)) {
            if (sCurrentMsg == PAUSE_ACTIVITY) {
                LogHelper.d("PinterParser", "pause end");
                TimeCounterManager.get().onActivityPaused();
            } else if (sCurrentMsg == LAUNCH_ACTIVITY) {
                LogHelper.d("PinterParser", "launch end");
                TimeCounterManager.get().onActivityCreated();
            }
            sCurrentMsg = 0;
        }
    }

    @Override
    public void println(String x) {
        parse(x);
    }
}

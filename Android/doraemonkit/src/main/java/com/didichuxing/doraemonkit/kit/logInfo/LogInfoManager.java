package com.didichuxing.doraemonkit.kit.logInfo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.didichuxing.doraemonkit.kit.logInfo.reader.LogcatReader;
import com.didichuxing.doraemonkit.kit.logInfo.reader.LogcatReaderLoader;
import com.didichuxing.doraemonkit.util.ExecutorUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wanglikun on 2018/10/10.
 */

public class LogInfoManager {
    private static final String TAG = "LogInfoManager";
    private static final int MESSAGE_PUBLISH_LOG = 1001;

    private OnLogCatchListener mListener;

    private LogCatchRunnable mTask;

    private static class Holder {
        private static LogInfoManager INSTANCE = new LogInfoManager();
    }

    private LogInfoManager() {
    }

    public static LogInfoManager getInstance() {
        return Holder.INSTANCE;
    }

    public void start() {
        if (mTask != null) {
            mTask.stop();
        }
        mTask = new LogCatchRunnable();
        ExecutorUtil.execute(mTask);
    }

    public void stop() {
        if (mTask != null) {
            mTask.stop();
        }
    }

    public interface OnLogCatchListener {
        void onLogCatch(List<LogLine> logLine);
    }

    public void registerListener(OnLogCatchListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PUBLISH_LOG: {
                    if (LogInfoManager.getInstance().mListener != null) {
                        LogInfoManager.getInstance().mListener.onLogCatch((List<LogLine>) msg.obj);
                    }
                }
                break;
                default:
                    break;
            }
        }
    }


    private static class LogCatchRunnable implements Runnable {
        private boolean isRunning = true;
        private Handler internalHandler;
        private LogcatReader mReader;
        private int mPid;

        private LogCatchRunnable() {
            internalHandler = new InternalHandler(Looper.getMainLooper());
            mPid = android.os.Process.myPid();
        }

        @Override
        public void run() {
            try {
                LogcatReaderLoader loader = LogcatReaderLoader.create(true);
                mReader = loader.loadReader();

                String line;
                int maxLines = 10000;
                LinkedList<LogLine> initialLines = new LinkedList<>();
                while ((line = mReader.readLine()) != null && isRunning) {
                    LogLine logLine = LogLine.newLogLine(line, false);
                    if (!mReader.readyToRecord()) {
                        if (logLine.getProcessId() == mPid) {
                            initialLines.add(logLine);
                        }
                        if (initialLines.size() > maxLines) {
                            initialLines.removeFirst();
                        }
                    } else if (!initialLines.isEmpty()) {
                        if (logLine.getProcessId() == mPid) {
                            initialLines.add(logLine);
                        }
                        Message message = Message.obtain();
                        message.what = MESSAGE_PUBLISH_LOG;
                        message.obj = new ArrayList<>(initialLines);
                        internalHandler.sendMessage(message);
                        initialLines.clear();
                    } else {
                        // just proceed as normal
                        if (logLine.getProcessId() == mPid) {
                            Message message = Message.obtain();
                            message.what = MESSAGE_PUBLISH_LOG;
                            message.obj = Collections.singletonList(logLine);
                            internalHandler.sendMessage(message);
                        }
                    }
                }
                mReader.killQuietly();
            } catch (IOException e) {
                LogHelper.e(TAG, e.toString());
            }
        }

        public void stop() {
            isRunning = false;
        }
    }
}
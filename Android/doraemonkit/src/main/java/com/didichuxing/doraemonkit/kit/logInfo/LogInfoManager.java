package com.didichuxing.doraemonkit.kit.logInfo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.didichuxing.doraemonkit.util.ExecutorUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        void onLogCatch(LogInfoItem infoItem);
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
                        LogInfoManager.getInstance().mListener.onLogCatch(new LogInfoItem((String) msg.obj));
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

        private LogCatchRunnable() {
            internalHandler = new InternalHandler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            try {
                Runtime.getRuntime().exec("logcat -c");
                Process process = Runtime.getRuntime().exec("logcat -v time");
                InputStream is = process.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);

                String log;
                while ((log = br.readLine()) != null && isRunning) {
                    Message message = Message.obtain();
                    message.what = MESSAGE_PUBLISH_LOG;
                    message.obj = log;
                    internalHandler.sendMessage(message);
                }

                br.close();
                reader.close();
                is.close();
            } catch (IOException e) {
                LogHelper.e(TAG, e.toString());
            }
        }

        public void stop() {
            isRunning = false;
        }
    }
}
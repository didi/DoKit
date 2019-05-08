package com.didichuxing.doraemonkit.ui.toast;

import android.os.Handler;
import android.os.Message;

/**
 * 延时器类
 *
 * @author zls
 *
 * @version 1.0
 *
 * @time 2015-12-27下午7:52:10
 */
public abstract class DelayTask {
    protected Thread thread;
    private boolean isRun;

    /**
     * 延时器
     */
    public DelayTask(final long delay) {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(delay);
                    if (isRun) {
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                }
            }
        };
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            logic();
        }
    };

    /**
     * 开始执行
     */
    public void start() {
        isRun = true;
        thread.start();
    }

    /**
     * 停止执行
     */
    public void stop() {
        isRun = false;
    }

    /**
     * 执行逻辑
     */
    public abstract void logic();

}
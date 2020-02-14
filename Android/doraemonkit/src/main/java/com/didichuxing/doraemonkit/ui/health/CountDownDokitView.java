package com.didichuxing.doraemonkit.ui.health;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-17:59
 * 描    述：页面倒计时浮标
 * 修订历史：
 * ================================================
 */
public class CountDownDokitView extends AbsDokitView {
    private static final String TAG = "CountDownDokitView";
    private TextView mNum;
    private CountDownTimer mCountDownTimer;
    private static int COUNT_DOWN_TOTAL = 10 * 1700;
    private static int COUNT_DOWN_INTERVAL = 1700;
    private Activity activity;

    @Override
    public void onCreate(Context context) {
        activity = ActivityUtils.getTopActivity();
    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_count_down, rootView, false);
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        mNum = findViewById(R.id.tv_number);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                mCountDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, COUNT_DOWN_INTERVAL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String value = String.valueOf((int) (millisUntilFinished / COUNT_DOWN_INTERVAL));
                        //LogHelper.i(TAG, "value===>" + value);
                        mNum.setText("" + value);
                    }

                    @Override
                    public void onFinish() {
                        mNum.setText("" + 0);
                        if (isNormalMode()) {
                            DokitViewManager.getInstance().detach(activity, CountDownDokitView.this);
                        } else {
                            DokitViewManager.getInstance().detach(CountDownDokitView.this);

                        }
                    }
                };
                //启动倒计时
                mCountDownTimer.start();
            }
        }, 1000);

    }

    /**
     * 重置倒计时
     */
    public void resetTime() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCountDownTimer != null) {
                    mCountDownTimer.start();
                }
            }
        }, 500);
    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = ConvertUtils.dp2px(280);
        params.y = ConvertUtils.dp2px(25);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        if (activity != null) {
            activity = null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

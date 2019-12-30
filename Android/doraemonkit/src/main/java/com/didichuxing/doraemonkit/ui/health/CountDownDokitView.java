package com.didichuxing.doraemonkit.ui.health;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;

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
    TextView mNum;
    CountDownTimer mCountDownTimer;
    private static int COUNT_DOWN_TOTAL = 10 * 1000;
    private static int COUNT_DOWN_INTERVAL = 1000;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_count_down, rootView, false);
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        mNum = findViewById(R.id.tv_number);
        mCountDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / COUNT_DOWN_INTERVAL));
                mNum.setText(value);
            }

            @Override
            public void onFinish() {
                DokitViewManager.getInstance().detach(CountDownDokitView.this);
            }
        };
        //启动倒计时
        mCountDownTimer.start();
    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = ConvertUtils.dp2px(300);
        params.y = ConvertUtils.dp2px(25);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

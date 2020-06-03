package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.util.DokitUtil;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/18-15:10
 * 描    述：弱网提示悬浮窗
 * 修订历史：
 * ================================================
 */
public class NetWokDokitView extends AbsDokitView {
    TextView mTvNetWork;
    TextView mTvTimeOutTime;
    TextView mTvRequestSpeed;
    TextView mTvResponseSpeed;
    LinearLayout mLlTimeWrap;
    LinearLayout mLlSpeedWrap;
    ImageView mIvClose;

    @Override

    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_network, rootView, false);
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        mTvNetWork = rootView.findViewById(R.id.tv_net_type);
        mTvTimeOutTime = rootView.findViewById(R.id.tv_time);
        mTvRequestSpeed = rootView.findViewById(R.id.tv_request_speed);
        mTvResponseSpeed = rootView.findViewById(R.id.tv_response_speed);
        mLlTimeWrap = rootView.findViewById(R.id.ll_timeout_wrap);
        mLlSpeedWrap = rootView.findViewById(R.id.ll_speed_wrap);
        mIvClose = rootView.findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeakNetworkManager.get().setActive(false);
                DokitViewManager.getInstance().detach(NetWokDokitView.class);
            }
        });
    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 100;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mTvNetWork == null) {
                return;
            }
            final int type = WeakNetworkManager.get().getType();
            switch (type) {
                case WeakNetworkManager.TYPE_TIMEOUT:
                    mTvNetWork.setText(DokitUtil.getString(R.string.dk_weaknet_type_timeout));
                    mTvTimeOutTime.setText("" + WeakNetworkManager.get().getTimeOutMillis() + " ms");
                    mLlTimeWrap.setVisibility(View.VISIBLE);
                    mLlSpeedWrap.setVisibility(View.GONE);

                    break;
                case WeakNetworkManager.TYPE_SPEED_LIMIT:
                    mTvNetWork.setText(DokitUtil.getString(R.string.dk_weaknet_type_speed));
                    mTvRequestSpeed.setText("" + WeakNetworkManager.get().getRequestSpeed() + " KB/S");
                    mTvResponseSpeed.setText("" + WeakNetworkManager.get().getResponseSpeed() + " KB/S");
                    mLlTimeWrap.setVisibility(View.GONE);
                    mLlSpeedWrap.setVisibility(View.VISIBLE);
                    break;
                default:
                    mTvNetWork.setText(DokitUtil.getString(R.string.dk_weaknet_type_off));
                    mLlTimeWrap.setVisibility(View.GONE);
                    mLlSpeedWrap.setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        invalidate();
    }


    @Override
    public void invalidate() {
        if (getRootView() == null) {
            return;
        }
        if (isNormalMode()) {
            FrameLayout.LayoutParams layoutParams = getNormalLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getRootView().setLayoutParams(layoutParams);
        } else {
            WindowManager.LayoutParams layoutParams = getSystemLayoutParams();
            if (layoutParams == null) {
                return;
            }
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowManager.updateViewLayout(getRootView(), layoutParams);
        }
    }
}

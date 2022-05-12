package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 关闭按钮 因为系统模式下 设置不响应事件  需要在盖一层用来响应事件
 * 修订历史：
 * ================================================
 */
public class PerformanceCloseDoKitView extends AbsDoKitView {
    LinearLayout mLlCloseWrap;
    FrameLayout mWrap0, mWrap1, mWrap2, mWrap3;
    ImageView mIvClose0, mIvClose1, mIvClose2, mIvClose3;
    PerformanceCloseListener mPerformanceCloseListener;

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dk_performance_close_wrap, rootView, false);
    }

    public void addItem(int index, int performanceType) {
        if (mLlCloseWrap == null) {
            return;
        }
        FrameLayout closeViewWrap = (FrameLayout) mLlCloseWrap.getChildAt(index);
        closeViewWrap.setVisibility(View.VISIBLE);
        closeViewWrap.setTag(performanceType);

    }

    public void removeItem(int index) {
        FrameLayout closeViewWrap = (FrameLayout) mLlCloseWrap.getChildAt(index);
        closeViewWrap.setVisibility(View.GONE);
        closeViewWrap.setTag(-1);
    }


    @Override
    public void onViewCreated(FrameLayout rootView) {
        mLlCloseWrap = findViewById(R.id.ll_close_wrap);
        mWrap0 = findViewById(R.id.fl_wrap0);
        mIvClose0 = findViewById(R.id.iv_close0);
        mWrap0.setVisibility(View.GONE);
        mWrap1 = findViewById(R.id.fl_wrap1);
        mIvClose1 = findViewById(R.id.iv_close1);
        mWrap1.setVisibility(View.GONE);
        mWrap2 = findViewById(R.id.fl_wrap2);
        mIvClose2 = findViewById(R.id.iv_close2);
        mWrap2.setVisibility(View.GONE);
        mWrap3 = findViewById(R.id.fl_wrap3);
        mIvClose3 = findViewById(R.id.iv_close3);
        mWrap3.setVisibility(View.GONE);

        mWrap0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                if (mPerformanceCloseListener != null) {
                    mPerformanceCloseListener.onClose((Integer) v.getTag());
                }
            }
        });
        mWrap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                if (mPerformanceCloseListener != null) {
                    mPerformanceCloseListener.onClose((Integer) v.getTag());
                }
            }
        });
        mWrap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                if (mPerformanceCloseListener != null) {
                    mPerformanceCloseListener.onClose((Integer) v.getTag());
                }
            }
        });
        mWrap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                if (mPerformanceCloseListener != null) {
                    mPerformanceCloseListener.onClose((Integer) v.getTag());
                }
            }
        });
    }

    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
    }

    protected void setPerformanceCloseListener(PerformanceCloseListener listener) {
        this.mPerformanceCloseListener = listener;
    }

    @Override
    public boolean canDrag() {
        return false;
    }
}

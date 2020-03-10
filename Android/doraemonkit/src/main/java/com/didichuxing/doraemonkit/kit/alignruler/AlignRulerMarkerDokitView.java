package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */

public class AlignRulerMarkerDokitView extends AbsDokitView {
    private List<OnAlignRulerMarkerPositionChangeListener> mPositionChangeListeners = new ArrayList<>();


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_marker, null);
    }

    @Override
    public void onViewCreated(FrameLayout view) {

    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
        params.width = DokitViewLayoutParams.WRAP_CONTENT;
        params.x = UIUtils.getWidthPixels() / 2;
        params.y = UIUtils.getHeightPixels() / 2;
    }

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removePositionChangeListeners();
    }

    @Override
    public void onMove(int x, int y, int dx, int dy) {
        super.onMove(x, y, dx, dy);
        for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
            if (isNormalMode()) {
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);
            }
        }
    }

    @Override
    public void updateViewLayout(String tag, boolean isActivityResume) {
        super.updateViewLayout(tag, isActivityResume);
        //更新标尺的位置信息
        for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
            if (isNormalMode()) {
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);
            }
        }

    }

    public interface OnAlignRulerMarkerPositionChangeListener {
        void onPositionChanged(int x, int y);
    }

    public void addPositionChangeListener(OnAlignRulerMarkerPositionChangeListener positionChangeListener) {
        mPositionChangeListeners.add(positionChangeListener);
        //更新标尺的位置信息
        for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
            if (isNormalMode()) {
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getRootView().getWidth() / 2, getNormalLayoutParams().topMargin + getRootView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getRootView().getWidth() / 2, getSystemLayoutParams().y + getRootView().getHeight() / 2);
            }
        }
    }

    public void removePositionChangeListener(OnAlignRulerMarkerPositionChangeListener positionChangeListener) {
        mPositionChangeListeners.remove(positionChangeListener);
    }

    private void removePositionChangeListeners() {
        mPositionChangeListeners.clear();
    }

    @Override
    public boolean restrictBorderline() {
        return false;
    }
}

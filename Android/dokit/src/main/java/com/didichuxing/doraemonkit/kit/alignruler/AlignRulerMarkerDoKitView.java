package com.didichuxing.doraemonkit.kit.alignruler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */

public class AlignRulerMarkerDoKitView extends AbsDoKitView {
    private List<OnAlignRulerMarkerPositionChangeListener> mPositionChangeListeners = new ArrayList<>();


    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_marker, null);
    }

    @Override
    public void onViewCreated(FrameLayout view) {

    }


    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
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
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getDoKitView().getWidth() / 2, getNormalLayoutParams().topMargin + getDoKitView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getDoKitView().getWidth() / 2, getSystemLayoutParams().y + getDoKitView().getHeight() / 2);
            }
        }
    }

    @Override
    public void updateViewLayout(String tag, boolean isActivityBackResume) {
        super.updateViewLayout(tag, isActivityBackResume);
        //更新标尺的位置信息
        for (OnAlignRulerMarkerPositionChangeListener listener : mPositionChangeListeners) {
            if (isNormalMode()) {
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getDoKitView().getWidth() / 2, getNormalLayoutParams().topMargin + getDoKitView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getDoKitView().getWidth() / 2, getSystemLayoutParams().y + getDoKitView().getHeight() / 2);
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
                listener.onPositionChanged(getNormalLayoutParams().leftMargin + getDoKitView().getWidth() / 2, getNormalLayoutParams().topMargin + getDoKitView().getHeight() / 2);
            } else {
                listener.onPositionChanged(getSystemLayoutParams().x + getDoKitView().getWidth() / 2, getSystemLayoutParams().y + getDoKitView().getHeight() / 2);
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

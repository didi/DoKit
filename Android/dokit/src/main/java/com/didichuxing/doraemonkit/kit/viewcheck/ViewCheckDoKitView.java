package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.BarUtils;
import com.didichuxing.doraemonkit.util.LifecycleListenerUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class ViewCheckDoKitView extends AbsDoKitView implements LifecycleListenerUtil.LifecycleListener {
    private static final String TAG = "ViewCheckFloatPage";

    private FindCheckViewRunnable mFindCheckViewRunnable;
    private HandlerThread mTraverHandlerThread;
    private Handler mTraverHandler;
    private List<OnViewSelectListener> mViewSelectListeners = new ArrayList<>();
    private Activity mResumedActivity;

    @Override
    public void onCreate(Context context) {
        mTraverHandlerThread = new HandlerThread(TAG);
        mTraverHandlerThread.start();
        mTraverHandler = new Handler(mTraverHandlerThread.getLooper());
        mFindCheckViewRunnable = new FindCheckViewRunnable();
        mResumedActivity = ActivityUtils.getTopActivity();
        LifecycleListenerUtil.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTraverHandler.removeCallbacks(mFindCheckViewRunnable);
        mTraverHandlerThread.quit();
        LifecycleListenerUtil.unRegisterListener(this);
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check, null);
    }

    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.x = UIUtils.getWidthPixels() / 2;
        params.y = UIUtils.getHeightPixels() / 2;
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
        params.width = DoKitViewLayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onUp(int x, int y) {
        super.onUp(x, y);
        preformFindCheckView();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mResumedActivity = activity;
        preformFindCheckView();
    }

    @Override
    public void onViewCreated(FrameLayout view) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onFragmentAttached(Fragment f) {

    }

    @Override
    public void onFragmentDetached(Fragment f) {

    }

    @Override
    public void onDown(int x, int y) {

    }

    void setViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.add(viewSelectListener);
        preformFindCheckView();
    }

    void removeViewSelectListener(OnViewSelectListener viewSelectListener) {
        mViewSelectListeners.remove(viewSelectListener);
    }

    void preformPreCheckView() {
        mFindCheckViewRunnable.mIndex--;
        if (mFindCheckViewRunnable.mIndex < 0) {
            mFindCheckViewRunnable.mIndex += mFindCheckViewRunnable.mCheckViewList.size();
        }
        mFindCheckViewRunnable.dispatchOnViewSelected();
    }

    void preformNextCheckView() {
        mFindCheckViewRunnable.mIndex++;
        if (mFindCheckViewRunnable.mIndex >= mFindCheckViewRunnable.mCheckViewList.size()) {
            mFindCheckViewRunnable.mIndex -= mFindCheckViewRunnable.mCheckViewList.size();
        }
        mFindCheckViewRunnable.dispatchOnViewSelected();
    }

    private void preformFindCheckView() {
        int x, y;
        if (isNormalMode()) {
            x = getNormalLayoutParams().leftMargin + getDoKitView().getWidth() / 2;
            if (BarUtils.isStatusBarVisible(getActivity())) {
                y = getNormalLayoutParams().topMargin + getDoKitView().getHeight() / 2 + BarUtils.getStatusBarHeight();
            } else {
                y = getNormalLayoutParams().topMargin + getDoKitView().getHeight() / 2;
            }
        } else {
            x = getSystemLayoutParams().x + getDoKitView().getWidth() / 2;
            if (BarUtils.isStatusBarVisible(getActivity())) {
                y = getSystemLayoutParams().y + getDoKitView().getHeight() / 2 + BarUtils.getStatusBarHeight();
            } else {
                y = getSystemLayoutParams().y + getDoKitView().getHeight() / 2;
            }
        }

        mTraverHandler.removeCallbacks(mFindCheckViewRunnable);
        mFindCheckViewRunnable.mX = x;
        mFindCheckViewRunnable.mY = y;
        mTraverHandler.post(mFindCheckViewRunnable);
    }

    private void traverseViews(List<View> viewList, View view, int x, int y) {
        if (view == null) {
            return;
        }

        int[] location = new int[2];
        //相对window的x y
        view.getLocationInWindow(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();

        // 深度优先遍历
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            if (childCount != 0) {
                for (int index = childCount - 1; index >= 0; index--) {
                    traverseViews(viewList, ((ViewGroup) view).getChildAt(index), x, y);
                }
            }
            //noinspection DuplicateExpressions
            if (left < x && x < right && top < y && y < bottom) {
                viewList.add(view);
            }
        } else {
            //noinspection DuplicateExpressions
            if (left < x && x < right && top < y && y < bottom) {
                viewList.add(view);
            }
        }
    }

    private void onViewSelected(View current, List<View> checkViewList) {
        for (OnViewSelectListener listener : mViewSelectListeners) {
            listener.onViewSelected(current, checkViewList);
        }
    }

    interface OnViewSelectListener {
        void onViewSelected(@Nullable View current, @NonNull List<View> checkViewList);
    }

    class FindCheckViewRunnable implements Runnable {

        private int mX = 0;
        private int mY = 0;
        private int mIndex = 0;
        private List<View> mCheckViewList;

        @Override
        public void run() {
            final List<View> viewList = new ArrayList<>(20);
            if (mResumedActivity != null && mResumedActivity.getWindow() != null) {
                if (isNormalMode()) {
                    //LogHelper.d(TAG, "x: " + mX + ", y: " + mY);
                    traverseViews(viewList, UIUtils.getDokitAppContentView(mResumedActivity), mX, mY);
                } else {
                    traverseViews(viewList, mResumedActivity.getWindow().getDecorView(), mX, mY);
                }
            }
            mIndex = 0;
            mCheckViewList = viewList;
            dispatchOnViewSelected();
        }

        private void dispatchOnViewSelected() {
            post(new Runnable() {
                @Override
                public void run() {
                    onViewSelected(getCurrentCheckView(), mCheckViewList);
                }
            });
        }

        private View getCurrentCheckView() {
            int size = mCheckViewList.size();
            if (size == 0) {
                return null;
            }
            return mCheckViewList.get(mIndex);
        }
    }

}

package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.didichuxing.doraemonkit.ui.FloatIconPage;

/**
 * Created by wanglikun on 2018/10/26.
 */

public class BaseFragment extends Fragment {
    private View mRootView;
    private int mContainer;

    @LayoutRes
    protected int onRequestLayout() {
        return 0;
    }

    final public <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    @Nullable
    @Override
    final public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        int id = onRequestLayout();
        if (id > 0) {
            mRootView = inflater.inflate(id, container, false);
        }
        if (mRootView == null) {
            mRootView = onCreateView(savedInstanceState);
        }
        if (interceptTouchEvents()) {
            if (mRootView != null) {
                mRootView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tryGetContainerId();
        try {
            if (view.getContext() instanceof Activity) {
                ((Activity) view.getContext()).getWindow().getDecorView().requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FloatPageManager.getInstance().removeAll(FloatIconPage.class);

    }

    private void tryGetContainerId() {
        if (mRootView != null) {
            View parent = (View) mRootView.getParent();
            if (parent != null) {
                mContainer = parent.getId();
            }
        }
    }

    protected View onCreateView(Bundle savedInstanceState) {
        return mRootView;
    }

    protected boolean interceptTouchEvents() {
        return false;
    }

    public int getContainer() {
        if (mContainer == 0) {
            tryGetContainerId();
        }
        return mContainer;
    }

    protected boolean onBackPressed() {
        return false;
    }

    public void showToast(CharSequence msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes int res) {
        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
    }

    public void showContent(Class<? extends BaseFragment> fragmentClass, Bundle bundle) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.showContent(fragmentClass, bundle);
        }
    }

    public void finish() {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            activity.doBack(this);
        }
    }

    public void showContent(Class<? extends BaseFragment> fragmentClass) {
        showContent(fragmentClass, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PageIntent intent = new PageIntent(FloatIconPage.class);
        intent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(intent);
    }
}
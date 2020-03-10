package com.didichuxing.doraemonkit.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayDeque;

/**
 * Created by wanglikun on 2018/10/26.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private ArrayDeque<BaseFragment> mFragments = new ArrayDeque<>();

    public void showContent(Class<? extends BaseFragment> target) {
        showContent(target, null);
    }

    public void showContent(Class<? extends BaseFragment> target, Bundle bundle) {
        try {
            BaseFragment fragment = target.newInstance();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(android.R.id.content, fragment);
            mFragments.push(fragment);
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mFragments.isEmpty()) {
            BaseFragment fragment = mFragments.getFirst();
            if (!fragment.onBackPressed()) {
                mFragments.removeFirst();
                super.onBackPressed();
                if (mFragments.isEmpty()) {
                    finish();
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    public void doBack(BaseFragment fragment) {
        if (mFragments.contains(fragment)) {
            mFragments.remove(fragment);
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack();
            if (mFragments.isEmpty()) {
                finish();
            }
        }
    }
}

package com.didichuxing.doraemonkit.weex.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.didichuxing.doraemonkit.ui.base.BaseActivity;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;

/**
 * @author haojianglong
 * @date 2019-06-18
 */
public class DKCommonActivity extends BaseActivity {

    private static final String CLASSNAME = "className";

    public static void startWith(Context context, Class<? extends BaseFragment> clazz) {
        Intent intent = new Intent(context, DKCommonActivity.class);
        intent.putExtra(CLASSNAME, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        Class<BaseFragment> clazz = (Class<BaseFragment>) getIntent().getSerializableExtra(CLASSNAME);
        showContent(clazz);
    }

}

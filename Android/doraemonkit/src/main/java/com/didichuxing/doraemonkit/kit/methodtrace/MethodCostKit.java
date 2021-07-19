package com.didichuxing.doraemonkit.kit.methodtrace;

import android.app.Activity;
import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.webview.CommWebViewFragment;
import com.didichuxing.doraemonkit.kit.webview.WebViewManager;
import com.google.auto.service.AutoService;

import org.jetbrains.annotations.NotNull;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-15-18:22
 * 描    述：
 * 修订历史：
 * ================================================
 */
@AutoService(AbstractKit.class)
public class MethodCostKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_method_cost;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_method_cost;
    }

    @Override
    public boolean onClickWithReturn(@NotNull Activity activity) {
        WebViewManager.INSTANCE.setUrl(NetworkManager.APP_DOCUMENT_URL);
        startUniversalActivity(CommWebViewFragment.class, activity, null, true);
        return true;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_performance_ck_method_coast";
    }
}

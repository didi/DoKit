package com.didichuxing.doraemonkit.kit.parameter;

import androidx.annotation.StringRes;

import com.didichuxing.doraemonkit.kit.parameter.AbsParameterFragment;
import com.didichuxing.doraemonkit.kit.performance.PerformanceDokitViewManager;
import com.didichuxing.doraemonkit.kit.performance.PerformanceFragmentCloseListener;

/**
 * didi Create on 2022/10/27 .
 * <p>
 * Copyright (c) 2022/10/27 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/10/27 12:19 下午
 * @Description 用一句话说明文件功能
 */

public abstract class AbsPerformanceParameterFragment extends AbsParameterFragment
    implements PerformanceFragmentCloseListener {


    protected void openChartPage(@StringRes int title, int type) {
        PerformanceDokitViewManager.open(type, getString(title), this);
    }


    protected void closeChartPage() {
        PerformanceDokitViewManager.close(getPerformanceType(), getString(getTitle()));
    }

    @Override
    public void onClose(int performanceType) {
        super.onClose(performanceType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除监听
        PerformanceDokitViewManager.onPerformanceSettingFragmentDestroy(this);
    }
}

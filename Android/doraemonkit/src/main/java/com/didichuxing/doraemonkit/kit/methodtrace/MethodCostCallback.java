package com.didichuxing.doraemonkit.kit.methodtrace;

import com.didichuxing.doraemonkit.kit.methodtrace.OrderBean;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-06-14:36
 * 描    述：
 * 修订历史：
 * ================================================
 */
public interface MethodCostCallback {
    /**
     *
     * @param orderBeans
     */
    void onCall(ArrayList<OrderBean> orderBeans);
}

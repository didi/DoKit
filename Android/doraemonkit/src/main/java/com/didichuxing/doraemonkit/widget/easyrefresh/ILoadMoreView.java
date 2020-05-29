package com.didichuxing.doraemonkit.widget.easyrefresh;

import android.view.View;

/**
 * Created by guanaj on 16/9/22.
 */

public interface ILoadMoreView {
    /**
     * 重置
     */
    void reset();

    /**
     * 加载中
     */
    void loading();

    /**
     * 加载完成
     */
    void loadComplete();

    void loadFail();

    void loadNothing();

    View getCanClickFailView();


}

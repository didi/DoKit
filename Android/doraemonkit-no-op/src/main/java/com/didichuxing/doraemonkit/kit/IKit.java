package com.didichuxing.doraemonkit.kit;

import android.content.Context;

/**
 * Created by zhangweida on 2018/6/22.
 */

public interface IKit {
    int getCategory();

    int getName();

    int getIcon();

    void onClick(Context context);

    void onAppInit(Context context);
}
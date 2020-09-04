package com.didichuxing.doraemonkit.widget.brvah.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

/**
 * @author: limuyang
 *  2019-12-03
 * @Description:
 */
public interface GridSpanSizeLookup {

    int getSpanSize(@NonNull GridLayoutManager gridLayoutManager, int viewType, int position);
}

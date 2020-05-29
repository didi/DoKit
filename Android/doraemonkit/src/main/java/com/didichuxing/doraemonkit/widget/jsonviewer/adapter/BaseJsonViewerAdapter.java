package com.didichuxing.doraemonkit.widget.jsonviewer.adapter;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yuyuhang on 2017/11/30.
 */
public abstract class BaseJsonViewerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public static int KEY_COLOR = 0xFF922799;
    public static int TEXT_COLOR = 0xFF3AB54A;
    public static int NUMBER_COLOR = 0xFF25AAE2;
    public static int BOOLEAN_COLOR = 0xFFF98280;
    public static int URL_COLOR = 0xFF66D2D5;
    public static int NULL_COLOR = 0xFFEF5935;
    public static int BRACES_COLOR = 0xFF4A555F;

    public static float TEXT_SIZE_DP = 12;
}

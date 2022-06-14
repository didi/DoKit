package com.didichuxing.doraemonkit.kit.permissionlist;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 通用的RecyclerView.ViewHolder。提供了根据viewId获取View的方法。
 * 提供了对View、TextView、ImageView的常用设置方法。
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    public boolean isExpanded;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        isExpanded=false;
    }

    /**
     * 设置、查看子项是否扩展
     * @return
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


    /**
     * 获取item对应的ViewDataBinding对象
     *
     * @param <T>
     * @return
     */
    public <T extends ViewDataBinding> T getBinding() {
        return DataBindingUtil.getBinding(this.itemView);
    }

    /**
     * 根据View Id 获取对应的View
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T get(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = this.itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    //******** 提供对View、TextView、ImageView的常用设置方法 ******//
    public String getText(int viewId) {
        TextView tv = get(viewId);
        return tv.getText().toString();
    }

    public BaseViewHolder setText(int viewId, CharSequence text) {
        TextView tv = get(viewId);
        tv.setText(text);
        return this;
    }

    public BaseViewHolder setText(int viewId, int textRes) {
        TextView tv = get(viewId);
        tv.setText(textRes);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = get(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setTextSize(int viewId, float size) {
        TextView view = get(viewId);
        view.setTextSize(size);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = get(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = get(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }


    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = get(viewId);
        view.setImageDrawable(drawable);
        return this;
    }


    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = get(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = get(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = get(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, int visible) {
        View view = get(viewId);
        view.setVisibility(visible);
        return this;
    }
}

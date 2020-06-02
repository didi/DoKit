package com.didichuxing.doraemonkit.widget.recyclerview;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 简单封装的适用于RecyclerView的ViewHolder
 *
 * @author Jin Liang
 * @since 16/1/5
 */
public abstract class AbsViewBinder<T> extends RecyclerView.ViewHolder {
    private T data;

    private View mView;

    public AbsViewBinder(final View view) {
        super(view);
        mView = view;
        getViews();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClick(view, data);
            }
        });
    }

    protected final View getView() {
        return mView;
    }

    protected abstract void getViews();

    public final <V extends View> V getView(@IdRes int id) {
        return (V) mView.findViewById(id);
    }

    public abstract void bind(T t);

    public void bind(T t, int position) {
        bind(t);
    }

    protected void onViewClick(View view, T data) {
    }

    protected final void setData(T data) {
        this.data = data;
    }

    protected final Context getContext() {
        return mView.getContext();
    }
}

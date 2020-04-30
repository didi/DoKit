package com.didichuxing.doraemonkit.widget.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 内置一个List的通用、简化的适用于RecyclerView的Adapter。
 * <p/>
 *
 * @author Jin Liang
 * @since 16/1/6
 */
public abstract class AbsRecyclerAdapter<T extends AbsViewBinder, V> extends RecyclerView.Adapter<T> {
    private static final String TAG = "AbsRecyclerAdapter";
    protected List<V> mList;
    private LayoutInflater mInflater;
    protected Context mContext;

    public AbsRecyclerAdapter(Context context) {
        if (context == null) {
            LogHelper.e(TAG, "Context should not be null");
            return;
        }
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public final T onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createView(mInflater, parent, viewType);
        return createViewHolder(view, viewType);
    }

    protected abstract T createViewHolder(View view, int viewType);

    /**
     * 如果是通过LayoutInflater创建的View,不要绑定到父View,RecyclerView会负责添加。
     *
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(T holder, int position) {
        V data = mList.get(position);
        holder.setData(data);
        holder.bind(data, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 列表末尾追加一个元素
     *
     * @param item
     */
    public void append(V item) {
        if (item == null) {
            return;
        }
        mList.add(item);
        notifyDataSetChanged();
    }

    /**
     * 在特定位置增加一个元素
     *
     * @param item
     * @param position
     */
    public void append(V item, int position) {
        if (item == null) {
            return;
        }
        if (position < 0) {
            position = 0;
        } else if (position > mList.size()) {
            position = mList.size();
        }
        mList.add(position, item);
        notifyDataSetChanged();
    }

    /**
     * 追加一个集合
     *
     * @param items
     */
    public final void append(Collection<V> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        mList.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 清空集合
     */
    public final void clear() {
        if (mList.isEmpty()) {
            return;
        }
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * 删除一个元素
     *
     * @param item
     */
    public final void remove(V item) {
        if (item == null) {
            return;
        }
        if (mList.contains(item)) {
            mList.remove(item);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除一个元素
     *
     * @param index
     */
    public final void remove(int index) {
        if (index < mList.size()) {
            mList.remove(index);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除一个集合
     *
     * @param items
     */
    public final void remove(Collection<V> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        if (mList.removeAll(items)) {
            notifyDataSetChanged();
        }
    }

    /**
     * 替换数据集合
     *
     * @param items
     */
    public void setData(Collection<V> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        if (mList.size() > 0) {
            mList.clear();
        }
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public List<V> getData() {
        return new ArrayList<>(mList);
    }
}

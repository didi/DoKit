package com.didichuxing.doraemonkit.kit.permissionlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.didichuxing.doraemonkit.R;

import java.util.ArrayList;


/**
 * 通用的分组列表Adapter。通过它可以很方便的实现列表的分组效果。
 * 这个类提供了一系列的对列表的更新、删除和插入等操作的方法。
 * 使用者要使用这些方法的列表进行操作，而不要直接使用RecyclerView.Adapter的方法。
 * 因为当分组列表发生变化时，需要及时更新分组列表的组结构{@link GroupedRecyclerViewAdapter#mStructures}
 */
public abstract class GroupedRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = R.integer.type_header;
    public static final int TYPE_FOOTER = R.integer.type_footer;
    public static final int TYPE_CHILD = R.integer.type_child;
    public static final int TYPE_EMPTY = R.integer.type_empty;

    private OnHeaderClickListener mOnHeaderClickListener;
    private OnFooterClickListener mOnFooterClickListener;
    private OnChildClickListener mOnChildClickListener;
    private OnHeaderLongClickListener mOnHeaderLongClickListener;
    private OnFooterLongClickListener mOnFooterLongClickListener;
    private OnChildLongClickListener mOnChildLongClickListener;

    protected Context mContext;
    //保存分组列表的组结构
    protected ArrayList<GroupStructure> mStructures = new ArrayList<>();
    //数据是否发生变化。如果数据发生变化，要及时更新组结构。
    private boolean isDataChanged;
    private int mTempPosition;

    private boolean mUseBinding;

    // 是否显示空布局
    private boolean showEmptyView = false;

    public GroupedRecyclerViewAdapter(Context context) {
        this(context, false);
    }

    public GroupedRecyclerViewAdapter(Context context, boolean useBinding) {
        mContext = context;
        mUseBinding = useBinding;
        registerAdapterDataObserver(new GroupDataObserver());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        structureChanged();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        //处理StaggeredGridLayout，保证组头和组尾占满一行。
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    private void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (isEmptyPosition(position) || judgeType(position) == TYPE_HEADER || judgeType(position) == TYPE_FOOTER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams)
                holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new BaseViewHolder(getEmptyView(parent));
        } else {
            if (mUseBinding) {
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    getLayoutId(mTempPosition, viewType), parent, false);
                return new BaseViewHolder(binding.getRoot());
            } else {
                View view = LayoutInflater.from(mContext).inflate(
                    getLayoutId(mTempPosition, viewType), parent, false);
                return new BaseViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int type = judgeType(position);
        final int groupPosition = getGroupPositionForPosition(position);
        if (type == TYPE_HEADER) {
            if (mOnHeaderClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnHeaderClickListener != null) {
                            ViewParent parent = holder.itemView.getParent();
                            int gPosition = parent instanceof FrameLayout ? groupPosition : getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                mOnHeaderClickListener.onHeaderClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition);
                            }
                        }
                    }
                });
            }

            if (mOnHeaderLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnHeaderLongClickListener != null) {
                            ViewParent parent = holder.itemView.getParent();
                            int gPosition = parent instanceof FrameLayout ? groupPosition : getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                return mOnHeaderLongClickListener.onHeaderLongClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition);
                            }
                        }
                        return false;
                    }
                });
            }
            onBindHeaderViewHolder((BaseViewHolder) holder, groupPosition);
        } else if (type == TYPE_FOOTER) {
            if (mOnFooterClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnFooterClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                mOnFooterClickListener.onFooterClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition);
                            }
                        }
                    }
                });
            }

            if (mOnFooterLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnFooterLongClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size()) {
                                return mOnFooterLongClickListener.onFooterLongClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition);
                            }
                        }
                        return false;
                    }
                });
            }
            onBindFooterViewHolder((BaseViewHolder) holder, groupPosition);
        } else if (type == TYPE_CHILD) {
            int childPosition = getChildPositionForPosition(groupPosition, position);
            if (mOnChildClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnChildClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            int cPosition = getChildPositionForPosition(gPosition, holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size() && cPosition >= 0
                                && cPosition < mStructures.get(gPosition).getChildrenCount()) {
                                mOnChildClickListener.onChildClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition, cPosition);
                            }
                        }
                    }
                });
            }

            if (mOnChildLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnChildLongClickListener != null) {
                            int gPosition = getGroupPositionForPosition(holder.getLayoutPosition());
                            int cPosition = getChildPositionForPosition(gPosition, holder.getLayoutPosition());
                            if (gPosition >= 0 && gPosition < mStructures.size() && cPosition >= 0
                                && cPosition < mStructures.get(gPosition).getChildrenCount()) {
                                return mOnChildLongClickListener.onChildLongClick(GroupedRecyclerViewAdapter.this,
                                    (BaseViewHolder) holder, gPosition, cPosition);
                            }
                        }
                        return false;
                    }
                });
            }
            onBindChildViewHolder((BaseViewHolder) holder, groupPosition, childPosition);
        }
    }

    @Override
    public int getItemCount() {
        if (isDataChanged) {
            structureChanged();
        }

        int count = count();
        if (count > 0) {
            return count;
        } else if (showEmptyView) {
            // 显示空布局
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isEmptyPosition(int position) {
        return position == 0 && showEmptyView && count() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmptyPosition(position)) {
            // 空布局
            return TYPE_EMPTY;
        }
        mTempPosition = position;
        int groupPosition = getGroupPositionForPosition(position);
        int type = judgeType(position);
        if (type == TYPE_HEADER) {
            return getHeaderViewType(groupPosition);
        } else if (type == TYPE_FOOTER) {
            return getFooterViewType(groupPosition);
        } else if (type == TYPE_CHILD) {
            int childPosition = getChildPositionForPosition(groupPosition, position);
            return getChildViewType(groupPosition, childPosition);
        }
        return super.getItemViewType(position);
    }

    public int getHeaderViewType(int groupPosition) {
        return TYPE_HEADER;
    }

    public int getFooterViewType(int groupPosition) {
        return TYPE_FOOTER;
    }

    public int getChildViewType(int groupPosition, int childPosition) {
        return TYPE_CHILD;
    }

    private int getLayoutId(int position, int viewType) {
        int type = judgeType(position);
        if (type == TYPE_HEADER) {
            return getHeaderLayout(viewType);
        } else if (type == TYPE_FOOTER) {
            return getFooterLayout(viewType);
        } else if (type == TYPE_CHILD) {
            return getChildLayout(viewType);
        }
        return 0;
    }

    private int count() {
        return countGroupRangeItem(0, mStructures.size());
    }

    /**
     * 判断item的type 头部 尾部 和 子项
     *
     * @param position
     * @return
     */
    public int judgeType(int position) {
        int itemCount = 0;
        int groupCount = mStructures.size();

        for (int i = 0; i < groupCount; i++) {
            GroupStructure structure = mStructures.get(i);
            if (structure.hasHeader()) {
                itemCount += 1;
                if (position < itemCount) {
                    return TYPE_HEADER;
                }
            }

            itemCount += structure.getChildrenCount();
            if (position < itemCount) {
                return TYPE_CHILD;
            }

            if (structure.hasFooter()) {
                itemCount += 1;
                if (position < itemCount) {
                    return TYPE_FOOTER;
                }
            }
        }

        return TYPE_EMPTY;
    }

    /**
     * 重置组结构列表
     */
    private void structureChanged() {
        mStructures.clear();
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            mStructures.add(new GroupStructure(hasHeader(i), hasFooter(i), getChildrenCount(i)));
        }
        isDataChanged = false;
    }

    /**
     * 根据下标计算position所在的组（groupPosition）
     *
     * @param position 下标
     * @return 组下标 groupPosition
     */
    public int getGroupPositionForPosition(int position) {
        int count = 0;
        int groupCount = mStructures.size();
        for (int i = 0; i < groupCount; i++) {
            count += countGroupItem(i);
            if (position < count) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据下标计算position在组中位置（childPosition）
     *
     * @param groupPosition 所在的组
     * @param position      下标
     * @return 子项下标 childPosition
     */
    public int getChildPositionForPosition(int groupPosition, int position) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            int itemCount = countGroupRangeItem(0, groupPosition + 1);
            GroupStructure structure = mStructures.get(groupPosition);
            int p = structure.getChildrenCount() - (itemCount - position)
                + (structure.hasFooter() ? 1 : 0);
            if (p >= 0) {
                return p;
            }
        }
        return -1;
    }

    /**
     * 获取一个组的开始下标，这个下标可能是组头，可能是子项(如果没有组头)或者组尾(如果这个组只有组尾)
     *
     * @param groupPosition 组下标
     * @return
     */
    public int getPositionForGroup(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            return countGroupRangeItem(0, groupPosition);
        } else {
            return -1;
        }

    }

    /**
     * 获取一个组的组头下标 如果该组没有组头 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    public int getPositionForGroupHeader(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            GroupStructure structure = mStructures.get(groupPosition);
            if (!structure.hasHeader()) {
                return -1;
            }
            return countGroupRangeItem(0, groupPosition);
        }
        return -1;
    }

    /**
     * 获取一个组的组尾下标 如果该组没有组尾 返回-1
     *
     * @param groupPosition 组下标
     * @return 下标
     */
    public int getPositionForGroupFooter(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            GroupStructure structure = mStructures.get(groupPosition);
            if (!structure.hasFooter()) {
                return -1;
            }
            return countGroupRangeItem(0, groupPosition + 1) - 1;
        }
        return -1;
    }

    /**
     * 获取一个组指定的子项下标 如果没有 返回-1
     *
     * @param groupPosition 组下标
     * @param childPosition 子项的组内下标
     * @return 下标
     */
    public int getPositionForChild(int groupPosition, int childPosition) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            GroupStructure structure = mStructures.get(groupPosition);
            if (structure.getChildrenCount() > childPosition) {
                int itemCount = countGroupRangeItem(0, groupPosition);
                return itemCount + childPosition + (structure.hasHeader() ? 1 : 0);
            }
        }
        return -1;
    }

    /**
     * 计算一个组里有多少个Item（头加尾加子项）
     *
     * @param groupPosition
     * @return
     */
    public int countGroupItem(int groupPosition) {
        int itemCount = 0;
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            GroupStructure structure = mStructures.get(groupPosition);
            if (structure.hasHeader()) {
                itemCount += 1;
            }
            itemCount += structure.getChildrenCount();
            if (structure.hasFooter()) {
                itemCount += 1;
            }
        }
        return itemCount;
    }

    /**
     * 计算多个组的项的总和
     *
     * @return
     */
    public int countGroupRangeItem(int start, int count) {
        int itemCount = 0;
        int size = mStructures.size();
        for (int i = start; i < size && i < start + count; i++) {
            itemCount += countGroupItem(i);
        }
        return itemCount;
    }

    /**
     * 设置空布局显示。默认不显示
     *
     * @param isShow
     */
    public void showEmptyView(boolean isShow) {
        if (isShow != showEmptyView) {
            showEmptyView = isShow;
            notifyDataChanged();
        }
    }

    public boolean isShowEmptyView() {
        return showEmptyView;
    }

    //****** 刷新操作 *****//

    /**
     * Use {@link #notifyDataChanged()} instead.
     */
    @Deprecated
    public void changeDataSet() {
        notifyDataChanged();
    }

    /**
     * 通知数据列表刷新
     */
    public void notifyDataChanged() {
        isDataChanged = true;
        notifyDataSetChanged();
    }

    /**
     * Use {@link #notifyGroupChanged(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void changeGroup(int groupPosition) {
        notifyGroupChanged(groupPosition);
    }

    /**
     * 通知一组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    public void notifyGroupChanged(int groupPosition) {
        int index = getPositionForGroup(groupPosition);
        int itemCount = countGroupItem(groupPosition);
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyGroupRangeChanged(int, int)} instead.
     *
     * @param groupPosition
     * @param count
     */
    @Deprecated
    public void changeRangeGroup(int groupPosition, int count) {
        notifyGroupRangeChanged(groupPosition, count);
    }

    /**
     * 通知多组数据刷新，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    public void notifyGroupRangeChanged(int groupPosition, int count) {
        int index = getPositionForGroup(groupPosition);
        int itemCount = 0;
        if (groupPosition + count <= mStructures.size()) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count);
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size());
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyHeaderChanged(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void changeHeader(int groupPosition) {
        notifyHeaderChanged(groupPosition);
    }

    /**
     * 通知组头刷新
     *
     * @param groupPosition
     */
    public void notifyHeaderChanged(int groupPosition) {
        int index = getPositionForGroupHeader(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * Use {@link #notifyFooterChanged(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void changeFooter(int groupPosition) {
        notifyFooterChanged(groupPosition);
    }

    /**
     * 通知组尾刷新
     *
     * @param groupPosition
     */
    public void notifyFooterChanged(int groupPosition) {
        int index = getPositionForGroupFooter(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * Use {@link #notifyChildChanged(int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     */
    @Deprecated
    public void changeChild(int groupPosition, int childPosition) {
        notifyChildChanged(groupPosition, childPosition);
    }

    /**
     * 通知一组里的某个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildChanged(int groupPosition, int childPosition) {
        int index = getPositionForChild(groupPosition, childPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * Use {@link #notifyChildRangeChanged(int, int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    @Deprecated
    public void changeRangeChild(int groupPosition, int childPosition, int count) {
        notifyChildRangeChanged(groupPosition, childPosition, count);
    }

    /**
     * 通知一组里的多个子项刷新
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    public void notifyChildRangeChanged(int groupPosition, int childPosition, int count) {
        if (groupPosition < mStructures.size()) {
            int index = getPositionForChild(groupPosition, childPosition);
            if (index >= 0) {
                GroupStructure structure = mStructures.get(groupPosition);
                if (structure.getChildrenCount() >= childPosition + count) {
                    notifyItemRangeChanged(index, count);
                } else {
                    notifyItemRangeChanged(index, structure.getChildrenCount() - childPosition);
                }
            }
        }
    }

    /**
     * Use {@link #notifyChildrenChanged(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void changeChildren(int groupPosition) {
        notifyChildrenChanged(groupPosition);
    }

    /**
     * 通知一组里的所有子项刷新
     *
     * @param groupPosition
     */
    public void notifyChildrenChanged(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mStructures.size()) {
            int index = getPositionForChild(groupPosition, 0);
            if (index >= 0) {
                GroupStructure structure = mStructures.get(groupPosition);
                notifyItemRangeChanged(index, structure.getChildrenCount());
            }
        }
    }

    //****** 删除操作 *****//

    /**
     * Use {@link #notifyDataRemoved()} instead.
     */
    @Deprecated
    public void removeAll() {
        notifyDataRemoved();
    }

    /**
     * 通知所有数据删除
     */
    public void notifyDataRemoved() {
        int count = countGroupRangeItem(0, mStructures.size());
        mStructures.clear();
        notifyItemRangeRemoved(0, count);
    }

    /**
     * Use {@link #notifyGroupRemoved(int)} instead.
     */
    @Deprecated
    public void removeGroup(int groupPosition) {
        notifyGroupRemoved(groupPosition);
    }

    /**
     * 通知一组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    public void notifyGroupRemoved(int groupPosition) {
        int index = getPositionForGroup(groupPosition);
        int itemCount = countGroupItem(groupPosition);
        if (index >= 0 && itemCount > 0) {
            mStructures.remove(groupPosition);
            notifyItemRangeRemoved(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyGroupRangeRemoved(int, int)} instead.
     */
    @Deprecated
    public void removeRangeGroup(int groupPosition, int count) {
        notifyGroupRangeRemoved(groupPosition, count);
    }

    /**
     * 通知多组数据删除，包括组头,组尾和子项
     *
     * @param groupPosition
     */
    public void notifyGroupRangeRemoved(int groupPosition, int count) {
        int index = getPositionForGroup(groupPosition);
        int itemCount = 0;
        if (groupPosition + count <= mStructures.size()) {
            itemCount = countGroupRangeItem(groupPosition, groupPosition + count);
        } else {
            itemCount = countGroupRangeItem(groupPosition, mStructures.size());
        }
        if (index >= 0 && itemCount > 0) {
            mStructures.remove(groupPosition);
            notifyItemRangeRemoved(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyHeaderRemoved(int)} instead.
     */
    @Deprecated
    public void removeHeader(int groupPosition) {
        notifyHeaderRemoved(groupPosition);
    }

    /**
     * 通知组头删除
     *
     * @param groupPosition
     */
    public void notifyHeaderRemoved(int groupPosition) {
        int index = getPositionForGroupHeader(groupPosition);
        if (index >= 0) {
            GroupStructure structure = mStructures.get(groupPosition);
            structure.setHasHeader(false);
            notifyItemRemoved(index);
        }
    }

    /**
     * Use {@link #notifyFooterRemoved(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void removeFooter(int groupPosition) {
        notifyFooterRemoved(groupPosition);
    }

    /**
     * 通知组尾删除
     *
     * @param groupPosition
     */
    public void notifyFooterRemoved(int groupPosition) {
        int index = getPositionForGroupFooter(groupPosition);
        if (index >= 0) {
            GroupStructure structure = mStructures.get(groupPosition);
            structure.setHasFooter(false);
            notifyItemRemoved(index);
        }
    }

    /**
     * Use {@link #notifyChildRemoved(int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     */
    @Deprecated
    public void removeChild(int groupPosition, int childPosition) {
        notifyChildRemoved(groupPosition, childPosition);
    }

    /**
     * 通知一组里的某个子项删除
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildRemoved(int groupPosition, int childPosition) {
        int index = getPositionForChild(groupPosition, childPosition);
        if (index >= 0) {
            GroupStructure structure = mStructures.get(groupPosition);
            structure.setChildrenCount(structure.getChildrenCount() - 1);
            notifyItemRemoved(index);
        }
    }

    /**
     * Use {@link #notifyChildRangeRemoved(int, int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    @Deprecated
    public void removeRangeChild(int groupPosition, int childPosition, int count) {
        notifyChildRangeRemoved(groupPosition, childPosition, count);
    }

    /**
     * 通知一组里的多个子项删除
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    public void notifyChildRangeRemoved(int groupPosition, int childPosition, int count) {
        if (groupPosition < mStructures.size()) {
            int index = getPositionForChild(groupPosition, childPosition);
            if (index >= 0) {
                GroupStructure structure = mStructures.get(groupPosition);
                int childCount = structure.getChildrenCount();
                int removeCount = count;
                if (childCount < childPosition + count) {
                    removeCount = childCount - childPosition;
                }
                structure.setChildrenCount(childCount - removeCount);
                notifyItemRangeRemoved(index, removeCount);
            }
        }
    }

    /**
     * Use {@link #notifyChildrenRemoved(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void removeChildren(int groupPosition) {
        notifyChildrenRemoved(groupPosition);
    }

    /**
     * 通知一组里的所有子项删除
     *
     * @param groupPosition
     */
    public void notifyChildrenRemoved(int groupPosition) {
        if (groupPosition < mStructures.size()) {
            int index = getPositionForChild(groupPosition, 0);
            if (index >= 0) {
                GroupStructure structure = mStructures.get(groupPosition);
                int itemCount = structure.getChildrenCount();
                structure.setChildrenCount(0);
                notifyItemRangeRemoved(index, itemCount);
            }
        }
    }

    //****** 插入操作 *****//

    /**
     * Use {@link #notifyGroupInserted(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void insertGroup(int groupPosition) {
        notifyGroupInserted(groupPosition);
    }

    /**
     * 通知一组数据插入
     *
     * @param groupPosition
     */
    public void notifyGroupInserted(int groupPosition) {
        GroupStructure structure = new GroupStructure(hasHeader(groupPosition),
            hasFooter(groupPosition), getChildrenCount(groupPosition));
        if (groupPosition < mStructures.size()) {
            mStructures.add(groupPosition, structure);
        } else {
            mStructures.add(structure);
            groupPosition = mStructures.size() - 1;
        }

        int index = countGroupRangeItem(0, groupPosition);
        int itemCount = countGroupItem(groupPosition);
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyGroupRangeInserted(int, int)} instead.
     *
     * @param groupPosition
     * @param count
     */
    @Deprecated
    public void insertRangeGroup(int groupPosition, int count) {
        notifyGroupRangeInserted(groupPosition, count);
    }

    /**
     * 通知多组数据插入
     *
     * @param groupPosition
     * @param count
     */
    public void notifyGroupRangeInserted(int groupPosition, int count) {
        ArrayList<GroupStructure> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            GroupStructure structure = new GroupStructure(hasHeader(i),
                hasFooter(i), getChildrenCount(i));
            list.add(structure);
        }

        if (groupPosition < mStructures.size()) {
            mStructures.addAll(groupPosition, list);
        } else {
            mStructures.addAll(list);
            groupPosition = mStructures.size() - list.size();
        }

        int index = countGroupRangeItem(0, groupPosition);
        int itemCount = countGroupRangeItem(groupPosition, count);
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount);
        }
    }

    /**
     * Use {@link #notifyHeaderInserted(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void insertHeader(int groupPosition) {
        notifyHeaderInserted(groupPosition);
    }

    /**
     * 通知组头插入
     *
     * @param groupPosition
     */
    public void notifyHeaderInserted(int groupPosition) {
        if (groupPosition < mStructures.size() && 0 > getPositionForGroupHeader(groupPosition)) {
            GroupStructure structure = mStructures.get(groupPosition);
            structure.setHasHeader(true);
            int index = countGroupRangeItem(0, groupPosition);
            notifyItemInserted(index);
        }
    }

    /**
     * Use {@link #notifyFooterInserted(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void insertFooter(int groupPosition) {
        notifyFooterInserted(groupPosition);
    }

    /**
     * 通知组尾插入
     *
     * @param groupPosition
     */
    public void notifyFooterInserted(int groupPosition) {
        if (groupPosition < mStructures.size() && 0 > getPositionForGroupFooter(groupPosition)) {
            GroupStructure structure = mStructures.get(groupPosition);
            structure.setHasFooter(true);
            int index = countGroupRangeItem(0, groupPosition + 1);
            notifyItemInserted(index);
        }
    }

    /**
     * Use {@link #notifyChildInserted(int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     */
    @Deprecated
    public void insertChild(int groupPosition, int childPosition) {
        notifyChildInserted(groupPosition, childPosition);
    }

    /**
     * 通知一个子项到组里插入
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildInserted(int groupPosition, int childPosition) {
        if (groupPosition < mStructures.size()) {
            GroupStructure structure = mStructures.get(groupPosition);
            int index = getPositionForChild(groupPosition, childPosition);
            if (index < 0) {
                index = countGroupRangeItem(0, groupPosition);
                index += structure.hasHeader() ? 1 : 0;
                index += structure.getChildrenCount();
            }
            structure.setChildrenCount(structure.getChildrenCount() + 1);
            notifyItemInserted(index);
        }
    }

    /**
     * Use {@link #notifyChildRangeInserted(int, int, int)} instead.
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    @Deprecated
    public void insertRangeChild(int groupPosition, int childPosition, int count) {
        notifyChildRangeInserted(groupPosition, childPosition, count);
    }

    /**
     * 通知一组里的多个子项插入
     *
     * @param groupPosition
     * @param childPosition
     * @param count
     */
    public void notifyChildRangeInserted(int groupPosition, int childPosition, int count) {
        if (groupPosition < mStructures.size()) {
            int index = countGroupRangeItem(0, groupPosition);
            GroupStructure structure = mStructures.get(groupPosition);
            if (structure.hasHeader()) {
                index++;
            }
            if (childPosition < structure.getChildrenCount()) {
                index += childPosition;
            } else {
                index += structure.getChildrenCount();
            }
            if (count > 0) {
                structure.setChildrenCount(structure.getChildrenCount() + count);
                notifyItemRangeInserted(index, count);
            }
        }
    }

    /**
     * Use {@link #notifyChildrenInserted(int)} instead.
     *
     * @param groupPosition
     */
    @Deprecated
    public void insertChildren(int groupPosition) {
        notifyChildrenInserted(groupPosition);
    }

    /**
     * 通知一组里的所有子项插入
     *
     * @param groupPosition
     */
    public void notifyChildrenInserted(int groupPosition) {
        if (groupPosition < mStructures.size()) {
            int index = countGroupRangeItem(0, groupPosition);
            GroupStructure structure = mStructures.get(groupPosition);
            if (structure.hasHeader()) {
                index++;
            }
            int itemCount = getChildrenCount(groupPosition);
            if (itemCount > 0) {
                structure.setChildrenCount(itemCount);
                notifyItemRangeInserted(index, itemCount);
            }
        }
    }

    //****** 设置点击事件 *****//

    /**
     * 设置组头点击事件
     *
     * @param listener
     */
    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    /**
     * 设置组尾点击事件
     *
     * @param listener
     */
    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mOnFooterClickListener = listener;
    }

    /**
     * 设置子项长按事件
     *
     * @param listener
     */
    public void setOnChildLongClickListener(OnChildLongClickListener listener) {
        mOnChildLongClickListener = listener;
    }

    /**
     * 设置组头长按事件
     *
     * @param listener
     */
    public void setOnHeaderLongClickListener(OnHeaderLongClickListener listener) {
        mOnHeaderLongClickListener = listener;
    }

    /**
     * 设置组尾长按事件
     *
     * @param listener
     */
    public void setOnFooterLongClickListener(OnFooterLongClickListener listener) {
        mOnFooterLongClickListener = listener;
    }

    /**
     * 设置子项点击事件
     *
     * @param listener
     */
    public void setOnChildClickListener(OnChildClickListener listener) {
        mOnChildClickListener = listener;
    }

    public abstract int getGroupCount();

    public abstract int getChildrenCount(int groupPosition);

    public abstract boolean hasHeader(int groupPosition);

    public abstract boolean hasFooter(int groupPosition);

    public abstract int getHeaderLayout(int viewType);

    public abstract int getFooterLayout(int viewType);

    public abstract int getChildLayout(int viewType);

    public abstract void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindChildViewHolder(BaseViewHolder holder,
                                               int groupPosition, int childPosition);

    /**
     * 获取空布局
     *
     * @param parent
     * @return
     */
    public View getEmptyView(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dk_permission_list_group_adapter_default_empty_view, parent, false);
        return view;
    }

    class GroupDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            isDataChanged = true;
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            isDataChanged = true;
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            isDataChanged = true;
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            isDataChanged = true;
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnFooterClickListener {
        void onFooterClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnChildClickListener {
        void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                          int groupPosition, int childPosition);
    }

    public interface OnHeaderLongClickListener {
        boolean onHeaderLongClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnFooterLongClickListener {
        boolean onFooterLongClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }

    public interface OnChildLongClickListener {
        boolean onChildLongClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                 int groupPosition, int childPosition);
    }
}

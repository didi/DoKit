package com.didichuxing.doraemonkit.ui.kit;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;

import java.util.List;

/**
 * Created by wanglikun on 2018/11/28.
 */

public class GroupKitAdapter extends AbsRecyclerAdapter<AbsViewBinder<List<KitItem>>, List<KitItem>> {

    public GroupKitAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<List<KitItem>> createViewHolder(View view, int viewType) {
        if (viewType == Category.CLOSE) {
            return new CloseKitViewHolder(view);
        } else {
            return new GroupKitViewHolder(view);
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == Category.CLOSE) {
            return inflater.inflate(R.layout.dk_item_close_kit, parent, false);
        } else {
            return inflater.inflate(R.layout.dk_item_group_kit, parent, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).get(0).kit.getCategory();
    }


    public class CloseKitViewHolder extends AbsViewBinder<List<KitItem>> {
        public CloseKitViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {

        }

        @Override
        public void bind(List<KitItem> kitItems) {

        }

        @Override
        protected void onViewClick(View view, List<KitItem> data) {
            super.onViewClick(view, data);
            for (KitItem item : data) {
                item.kit.onClick(getContext());
            }
        }
    }

    public class GroupKitViewHolder extends AbsViewBinder<List<KitItem>> {
        private TextView name;
        private RecyclerView kitContainer;
        private KitAdapter kitAdapter;

        public GroupKitViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            name = getView(R.id.name);
            kitContainer = getView(R.id.group_kit_container);
        }

        @Override
        public void bind(final List<KitItem> kitItems) {
            switch (kitItems.get(0).kit.getCategory()) {
                case Category.BIZ: {
                    name.setText(R.string.dk_category_biz);
                    break;
                }
                case Category.PERFORMANCE: {
                    name.setText(R.string.dk_category_performance);
                    break;
                }
                case Category.TOOLS: {
                    name.setText(R.string.dk_category_tools);
                    break;
                }
                case Category.UI: {
                    name.setText(R.string.dk_category_ui);
                    break;
                }
                default:
                    break;
            }
            kitContainer.setLayoutManager(new GridLayoutManager(getContext(), 4));
            kitAdapter = new KitAdapter(getContext());
            kitAdapter.setData(kitItems);
            kitContainer.setAdapter(kitAdapter);
        }
    }
}

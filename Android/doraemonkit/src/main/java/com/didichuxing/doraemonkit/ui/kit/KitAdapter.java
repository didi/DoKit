package com.didichuxing.doraemonkit.ui.kit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.datapick.DataPickManager;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * Created by wanglikun on 2018/9/14.
 * 每个分类的adapter
 */

public class KitAdapter extends AbsRecyclerAdapter<AbsViewBinder<KitItem>, KitItem> {
    private static final String TAG = "KitAdapter";

    public KitAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<KitItem> createViewHolder(View view, int viewType) {
        return new KitViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_kit, parent, false);
    }

    public class KitViewHolder extends AbsViewBinder<KitItem> {
        private ImageView mIcon;
        private TextView mName;

        public KitViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mIcon = getView(R.id.icon);
            mName = getView(R.id.name);
        }

        @Override
        public void bind(final KitItem kitItem) {
            mName.setText(kitItem.kit.getName());
            mIcon.setImageResource(kitItem.kit.getIcon());
        }

        @Override
        protected void onViewClick(View view, final KitItem data) {
            super.onViewClick(view, data);
            //常规模式下点击常用工具不隐藏工具面板
            //if (data.kit.getCategory() != Category.TOOLS && DoraemonKit.IS_NORMAL_FLOAT_MODE) {
            DokitViewManager.getInstance().detachToolPanel();
            //}
            data.kit.onClick(getContext());
            try {
                //添加埋点
                DataPickManager.getInstance().addData("click", data.kit.getCategory(), getContext().getString(data.kit.getName()));
            } catch (Exception e) {
                LogHelper.i(TAG, "error===>" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

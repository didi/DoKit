package com.didichuxing.doraemonkit.ui.kit;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

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
        } else if (viewType == Category.VERSION) {
            return new VersionKitViewHolder(view);
        } else if (viewType == Category.FLOAT_MODE) {
            return new ModeKitViewHolder(view);
        } else {
            return new GroupKitViewHolder(view);
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == Category.CLOSE) {
            return inflater.inflate(R.layout.dk_item_close_kit, parent, false);
        } else if (viewType == Category.VERSION) {
            return inflater.inflate(R.layout.dk_item_version_kit, parent, false);
        } else if (viewType == Category.FLOAT_MODE) {
            return inflater.inflate(R.layout.dk_item_group_mode, parent, false);
        } else {
            return inflater.inflate(R.layout.dk_item_group_kit, parent, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getData().get(position) == null || getData().get(position).size() == 0) {
            return 0;
        }
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


    public class VersionKitViewHolder extends AbsViewBinder<List<KitItem>> {
        private TextView name;

        public VersionKitViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            name = getView(R.id.version);
            //适配无法准确获取底部导航栏高度的bug
            if (name.getParent() != null) {
                ((ViewGroup) name.getParent()).setPadding(0, 0, 0, BarUtils.getNavBarHeight());
            }
        }

        @Override
        public void bind(List<KitItem> kitItems) {
            String version = mContext.getString(R.string.dk_kit_version);
            name.setText(String.format(version, BuildConfig.VERSION_NAME));
        }

    }

    public class ModeKitViewHolder extends AbsViewBinder<List<KitItem>> {
        private RadioGroup radioGroup;
        private RadioButton rbNormal;
        private RadioButton rbSystem;

        public ModeKitViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            radioGroup = getView(R.id.rb_group);
            rbNormal = getView(R.id.rb_normal);
            rbSystem = getView(R.id.rb_system);
        }

        @Override
        public void bind(List<KitItem> kitItems) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rb_normal) {
                        //选中normal
                        SharedPrefsUtil.putString(mContext, SharedPrefsKey.FLOAT_START_MODE, "normal");
                    } else {
                        //选中系统
                        SharedPrefsUtil.putString(mContext, SharedPrefsKey.FLOAT_START_MODE, "system");
                    }
                }
            });

            rbNormal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbNormal.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.relaunchApp();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }, 500);
                }
            });

            rbSystem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbSystem.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.relaunchApp();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }, 500);
                }
            });

            String floatMode = SharedPrefsUtil.getString(mContext, SharedPrefsKey.FLOAT_START_MODE, "normal");
            if (floatMode.equals("normal")) {
                rbNormal.setChecked(true);
            } else {
                rbSystem.setChecked(true);
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
            if (kitItems == null || kitItems.size() == 0) {
                return;
            }
            switch (kitItems.get(0).kit.getCategory()) {
                case Category.BIZ:
                    name.setText(R.string.dk_category_biz);
                    break;

                case Category.WEEX:
                    name.setText(R.string.dk_category_weex);
                    break;
                case Category.PERFORMANCE:
                    name.setText(R.string.dk_category_performance);
                    break;

                case Category.TOOLS:
                    name.setText(R.string.dk_category_tools);
                    break;

                case Category.UI:
                    name.setText(R.string.dk_category_ui);
                    break;

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

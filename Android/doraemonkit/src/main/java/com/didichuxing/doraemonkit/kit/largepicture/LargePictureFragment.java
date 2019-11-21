package com.didichuxing.doraemonkit.kit.largepicture;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceMemoryInfoConfig;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

/**
 * 大图功能检测
 */
public class LargePictureFragment extends BaseFragment {
    private LargePictureItemAdapter mSettingItemAdapter;
    private RecyclerView mSettingList;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_performance_large_picture_setting;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.dk_category_large_image);
        TextView tvDesc = findViewById(R.id.tv_desc);
        tvDesc.setText(Html.fromHtml(getResources().getString(R.string.dk_large_picture_threshold_desc)));

        EditText fileEditText = findViewById(R.id.ed_file_threshold);
        fileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        ToastUtils.showShort("value can not null");
                        return;
                    }
                    if (Float.parseFloat(s.toString()) < 0) {
                        ToastUtils.showShort("value can not  < 0");
                        return;
                    }
                    float value = Float.parseFloat(s.toString());
                    PerformanceSpInfoConfig.setLargeImgFileThreshold(getActivity(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText memoryEditText = findViewById(R.id.ed_memory_threshold);
        memoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        ToastUtils.showShort("value can not null");
                        return;
                    }
                    if (Float.parseFloat(s.toString()) < 0) {
                        ToastUtils.showShort("value can not  < 0");
                        return;
                    }
                    float value = Float.parseFloat(s.toString());
                    PerformanceSpInfoConfig.setLargeImgMemoryThreshold(getActivity(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        float fileThreshold = PerformanceSpInfoConfig.getLargeImgFileThreshold(getActivity(), LargePictureManager.FILE_DEFAULT_THRESHOLD);
        fileEditText.setText("" + fileThreshold);

        float memoryThreshold = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(getActivity(), LargePictureManager.MEMORY_DEFAULT_THRESHOLD);
        memoryEditText.setText("" + memoryThreshold);


        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new LargePictureItemAdapter(getContext());
        mSettingItemAdapter.append(new SettingItem(R.string.dk_large_picture_switch, PerformanceSpInfoConfig.isLargeImgOpen()));
        mSettingItemAdapter.append(new SettingItem(R.string.dk_large_picture_look, R.drawable.dk_more_icon));

        mSettingItemAdapter.setOnSettingItemSwitchListener(new LargePictureItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_large_picture_switch) {
                    PerformanceSpInfoConfig.setLargeImgOpen(on);
                    if (on) {
                        if (!NetworkManager.isActive()) {
                            NetworkManager.get().startMonitor();
                        }
                    } else {
                        NetworkManager.get().stopMonitor();
                    }
                }
            }
        });
        mSettingItemAdapter.setOnSettingItemClickListener(new LargePictureItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_large_picture_look) {
                    showContent(LargeImageListFragment.class);
                }
            }
        });
        mSettingList.setAdapter(mSettingItemAdapter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}

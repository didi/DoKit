package com.didichuxing.doraemonkit.kit.largepicture;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

import java.text.DecimalFormat;

/**
 * 大图功能检测
 */
public class LargePictureFragment extends BaseFragment {
    private LargePictureItemAdapter mSettingItemAdapter;
    private RecyclerView mSettingList;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

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
        //TextView tvDesc = findViewById(R.id.tv_desc);
        //tvDesc.setText(Html.fromHtml(getResources().getString(R.string.dk_large_picture_threshold_desc)));

        EditText fileEditText = findViewById(R.id.ed_file_threshold);
        fileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
                    float formateValue = Float.parseFloat(mDecimalFormat.format(value));
                    //设置文件大小
                    PerformanceSpInfoConfig.setLargeImgFileThreshold(formateValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        EditText memoryEditText = findViewById(R.id.ed_memory_threshold);
        memoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
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
                    float formateValue = Float.parseFloat(mDecimalFormat.format(value));
                    //设置内存大小
                    PerformanceSpInfoConfig.setLargeImgMemoryThreshold(formateValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        double fileThreshold = PerformanceSpInfoConfig.getLargeImgFileThreshold(LargePictureManager.FILE_DEFAULT_THRESHOLD);
        fileEditText.setText(mDecimalFormat.format(fileThreshold));

        double memoryThreshold = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(LargePictureManager.MEMORY_DEFAULT_THRESHOLD);
        memoryEditText.setText(mDecimalFormat.format(memoryThreshold));


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
        mSettingItemAdapter.append(new SettingItem(R.string.dk_large_picture_look, R.mipmap.dk_more_icon));

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
                        //清空缓存
                        LargePictureManager.LARGE_IMAGE_INFO_MAP.clear();
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

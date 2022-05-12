package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

/**
 * 模拟弱网
 * <p>
 * Created by xiandanin on 2019/5/7 19:10
 */
public class WeakNetworkFragment extends BaseFragment implements TextWatcher {
    private SettingItemAdapter mSettingItemAdapter;
    private RecyclerView mSettingList;
    private View mWeakNetworkOptionView;
    private View mTimeoutOptionView;
    private View mSpeedLimitView;
    private EditText mTimeoutValueView, mRequestSpeedView, mResponseSpeedView;
    private AbsDoKitView mNetWorkDokitView;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_weak_network;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        HomeTitleBar homeTitleBar = findViewById(R.id.title_bar);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        mWeakNetworkOptionView = findViewById(R.id.weak_network_layout);
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingList.setAdapter(mSettingItemAdapter);
        mSettingItemAdapter.append(new SettingItem(R.string.dk_weak_network_switch, WeakNetworkManager.get().isActive()));
        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (data.desc == R.string.dk_weak_network_switch) {
                    setWeakNetworkEnabled(data.isChecked);
                }
            }
        });
        RadioGroup optionGroup = findViewById(R.id.weak_network_option);
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (R.id.timeout == checkedId) {
                    //超时
                    showTimeoutOptionView();
                } else if (R.id.speed_limit == checkedId) {
                    //限速
                    showSpeedLimitOptionView();
                } else {
                    //断网
                    showOffNetworkOptionView();
                }

                if (mNetWorkDokitView == null) {
                    mNetWorkDokitView = DoKit.getDoKitView(getActivity(), NetWokDoKitView.class);
                }
                if (mNetWorkDokitView != null) {
                    //重新调用刷新
                    mNetWorkDokitView.onResume();
                }

            }
        });
        mTimeoutOptionView = findViewById(R.id.layout_timeout_option);
        mSpeedLimitView = findViewById(R.id.layout_speed_limit);

        mTimeoutValueView = findViewById(R.id.value_timeout);
        mTimeoutValueView.addTextChangedListener(this);
        mRequestSpeedView = findViewById(R.id.request_speed);
        mRequestSpeedView.addTextChangedListener(this);
        mResponseSpeedView = findViewById(R.id.response_speed);
        mResponseSpeedView.addTextChangedListener(this);

        updateUIState();
    }

    private void updateUIState() {
        final boolean active = WeakNetworkManager.get().isActive();
        mWeakNetworkOptionView.setVisibility(active ? View.VISIBLE : View.GONE);
        if (active) {
            int checkButtonId;
            final int type = WeakNetworkManager.get().getType();
            switch (type) {
                case WeakNetworkManager.TYPE_TIMEOUT:
                    checkButtonId = R.id.timeout;
                    break;
                case WeakNetworkManager.TYPE_SPEED_LIMIT:
                    checkButtonId = R.id.speed_limit;
                    break;
                default:
                    checkButtonId = R.id.off_network;
                    break;
            }
            RadioButton defaultOptionView = findViewById(checkButtonId);
            defaultOptionView.setChecked(true);

            mTimeoutValueView.setHint(String.valueOf(WeakNetworkManager.get().getTimeOutMillis()));
            mRequestSpeedView.setHint(String.valueOf(WeakNetworkManager.get().getRequestSpeed()));
            mResponseSpeedView.setHint(String.valueOf(WeakNetworkManager.get().getResponseSpeed()));
        }
    }

    private void setWeakNetworkEnabled(boolean enabled) {
        WeakNetworkManager.get().setActive(enabled);
        updateUIState();
        if (enabled) {
            DoKit.launchFloating(NetWokDoKitView.class);
        } else {
            DoKit.removeFloating(NetWokDoKitView.class);
        }
    }

    private void showTimeoutOptionView() {
        mTimeoutOptionView.setVisibility(View.VISIBLE);
        mSpeedLimitView.setVisibility(View.GONE);

        WeakNetworkManager.get().setType(WeakNetworkManager.TYPE_TIMEOUT);
    }

    private void showSpeedLimitOptionView() {
        mSpeedLimitView.setVisibility(View.VISIBLE);
        mTimeoutOptionView.setVisibility(View.GONE);

        WeakNetworkManager.get().setType(WeakNetworkManager.TYPE_SPEED_LIMIT);
    }

    private void showOffNetworkOptionView() {
        mSpeedLimitView.setVisibility(View.GONE);
        mTimeoutOptionView.setVisibility(View.GONE);

        WeakNetworkManager.get().setType(WeakNetworkManager.TYPE_OFF_NETWORK);
    }

    private long getLongValue(EditText editText) {
        CharSequence text = editText.getText();
        if (TextUtils.isEmpty(text)) {
            return 0L;
        }
        return Long.parseLong(text.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        long timeOutMillis = getLongValue(mTimeoutValueView);
        long requestSpeed = getLongValue(mRequestSpeedView);
        long responseSpeed = getLongValue(mResponseSpeedView);
        WeakNetworkManager.get().setParameter(timeOutMillis, requestSpeed, responseSpeed);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}

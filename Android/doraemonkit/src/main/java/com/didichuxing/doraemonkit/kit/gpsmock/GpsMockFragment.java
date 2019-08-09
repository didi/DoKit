package com.didichuxing.doraemonkit.kit.gpsmock;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.core.model.LatLng;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.ui.widget.webview.MyWebView;
import com.didichuxing.doraemonkit.ui.widget.webview.MyWebViewClient;
import com.didichuxing.doraemonkit.util.WebUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMockFragment extends BaseFragment implements SettingItemAdapter.OnSettingItemSwitchListener, MyWebViewClient.InvokeListener {
    private static final String TAG = "GpsMockFragment";

    private HomeTitleBar mTitleBar;
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;
    private EditText mLongitude;
    private EditText mLatitude;
    private TextView mMockLocationBtn;
    private MyWebView mWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intiSettingList();
        initTitleBar();
        initMockLocationArea();
        initWebView();
    }

    private void initWebView() {
        mWebView = findViewById(R.id.web_view);
        WebUtil.webViewLoadLocalHtml(mWebView, "html/map.html");
        mWebView.addInvokeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeInvokeListener(this);
    }

    private void initMockLocationArea() {
        mLongitude = findViewById(R.id.longitude);
        mLatitude = findViewById(R.id.latitude);
        mLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput()) {
                    mMockLocationBtn.setEnabled(true);
                } else {
                    mMockLocationBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput()) {
                    mMockLocationBtn.setEnabled(true);
                } else {
                    mMockLocationBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mMockLocationBtn = findViewById(R.id.mock_location);
        mMockLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }
                double latitude = Double.valueOf(mLatitude.getText().toString());
                double longitude = Double.valueOf(mLongitude.getText().toString());
                GpsMockManager.getInstance().mockLocation(latitude, longitude);
                GpsMockConfig.saveMockLocation(getContext(), new LatLng(latitude, longitude));
                Toast.makeText(getContext(), getString(R.string.dk_gps_location_change_toast, mLatitude.getText(), mLongitude.getText()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(mLongitude.getText().toString())) {
            return false;
        }
        if (TextUtils.isEmpty(mLatitude.getText().toString())) {
            return false;
        }
        double longitude = Double.valueOf(mLongitude.getText().toString());
        double latitude = Double.valueOf(mLatitude.getText().toString());
        if (longitude > 180 || longitude < -180) {
            return false;
        }
        if (latitude > 90 || latitude < -90) {
            return false;
        }
        return true;
    }

    private void initTitleBar() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
    }

    private void intiSettingList() {
        mSettingList = findViewById(R.id.setting_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mSettingList.setLayoutManager(layoutManager);
        List<SettingItem> settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.string.dk_gpsmock_open,
                GpsMockConfig.isGPSMockOpen(getContext())));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.setData(settingItems);
        mSettingItemAdapter.setOnSettingItemSwitchListener(this);
        mSettingList.setAdapter(mSettingItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mSettingList.addItemDecoration(decoration);
    }

    @Override
    public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
        if (data.desc == R.string.dk_gpsmock_open) {
            GpsMockConfig.setGPSMockOpen(getContext(), on);
            if (on) {
                GpsMockManager.getInstance().startMock();
            } else {
                GpsMockManager.getInstance().stopMock();
            }
        }
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_gps_mock;
    }

    @Override
    public void onNativeInvoke(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri = Uri.parse(url);
        String lastPath = uri.getLastPathSegment();
        if (!"sendLocation".equals(lastPath)) {
            return;
        }
        String lat = uri.getQueryParameter("lat");
        String lnt = uri.getQueryParameter("lng");
        if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lnt)) {
            return;
        }
        mLatitude.setText(lat);
        mLongitude.setText(lnt);
    }
}
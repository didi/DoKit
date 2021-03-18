package com.didichuxing.doraemonkit.kit.gpsmock;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.hook.AMapClientLastLocationHook;
import com.didichuxing.doraemonkit.model.LatLng;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.widget.webview.MyWebView;
import com.didichuxing.doraemonkit.widget.webview.MyWebViewClient;
import com.didichuxing.doraemonkit.util.WebUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.DexposedBridge;

/**
 * Created by wanglikun on 2018/9/20.
 * gps mock
 */

public class GpsMockFragment extends BaseFragment implements SettingItemAdapter.OnSettingItemSwitchListener, MyWebViewClient.InvokeListener {
    private static final String TAG = "GpsMockFragment";

    private HomeTitleBar mTitleBar;
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;
    //    private EditText mLongitude;
//    private EditText mLatitude;
//    private TextView mMockLocationBtn;
    private ImageView mIvSearch;
    private EditText mEdLongLat;
    private MyWebView mWebView;
    private boolean isInit = true;

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
        mWebView = findViewById(R.id.webview);
        WebUtil.webViewLoadLocalHtml(mWebView, "map/map.html");
        mWebView.addInvokeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeInvokeListener(this);
    }

    private void initMockLocationArea() {
        mEdLongLat = findViewById(R.id.ed_long_lat);
        mIvSearch = findViewById(R.id.iv_search);


        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInput()) {
                    return;
                }
                String strLongLat = mEdLongLat.getText().toString();
                String[] longAndLat = strLongLat.split(" ");
                double longitude, latitude;
                try {
                    longitude = Double.valueOf(longAndLat[0]);
                    latitude = Double.valueOf(longAndLat[1]);
                } catch (Exception e) {
                    ToastUtils.showShort("经纬度必须为数字");
                    return;
                }

                GpsMockManager.getInstance().mockLocationWithNotify(latitude, longitude);
                GpsMockConfig.saveMockLocation(new LatLng(latitude, longitude));
                //刷新地图
                String url = String.format("javascript:updateLocation(%s,%s)", latitude, longitude);
                mWebView.loadUrl(url);
                ToastUtils.showShort(getString(R.string.dk_gps_location_change_toast, "" + longitude, "" + latitude));
            }
        });
    }

    private boolean checkInput() {
        String strLongLat = mEdLongLat.getText().toString();
        if (TextUtils.isEmpty(strLongLat)) {
            ToastUtils.showShort("请输入经纬度");
            return false;
        }
        String[] longAndLat = strLongLat.split(" ");
        if (longAndLat.length != 2) {
            ToastUtils.showShort("请输入符合规范的经纬度格式");
            return false;
        }

        if (TextUtils.isEmpty(longAndLat[0])) {
            return false;
        }
        if (TextUtils.isEmpty(longAndLat[1])) {
            return false;
        }
        double longitude, latitude;
        try {
            longitude = Double.valueOf(longAndLat[0]);
            latitude = Double.valueOf(longAndLat[1]);
        } catch (Exception e) {
            ToastUtils.showShort("经纬度必须为数字");
            return false;
        }

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
                GpsMockConfig.isGPSMockOpen()));
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
            if (on) {
                GpsMockManager.getInstance().startMock();
            } else {
                GpsMockManager.getInstance().stopMock();
            }
            GpsMockConfig.setGPSMockOpen(on);
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
        mEdLongLat.setText(String.format("%s %s", lnt, lat));
        if (!isInit) {
            double longitude, latitude;
            try {
                //保存当前的经纬度
                longitude = Double.valueOf(lnt);
                latitude = Double.valueOf(lat);
            } catch (Exception e) {
                ToastUtils.showShort("经纬度必须为数字");
                return;
            }

            GpsMockManager.getInstance().mockLocationWithNotify(latitude, longitude);
            GpsMockConfig.saveMockLocation(new LatLng(latitude, longitude));
            ToastUtils.showShort(getString(R.string.dk_gps_location_change_toast, "" + longitude, "" + latitude));

        }
        isInit = false;

    }


}
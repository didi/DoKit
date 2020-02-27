package com.didichuxing.doraemonkit.kit.sysinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.DeviceUtils;
import com.didichuxing.doraemonkit.util.ExecutorUtil;
import com.didichuxing.doraemonkit.util.PermissionUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机app信息
 * Created by zhangweida on 2018/6/25.
 */

public class SysInfoFragment extends BaseFragment {
    private RecyclerView mInfoList;
    private SysInfoItemAdapter mInfoItemAdapter;


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_sys_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mInfoList = findViewById(R.id.info_list);
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                getActivity().finish();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mInfoList.setLayoutManager(layoutManager);
        mInfoItemAdapter = new SysInfoItemAdapter(getContext());
        mInfoList.setAdapter(mInfoItemAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mInfoList.addItemDecoration(decoration);
    }

    private void initData() {
        List<SysInfoItem> sysInfoItems = new ArrayList<>();
        addAppData(sysInfoItems);
        addDeviceData(sysInfoItems);
        if (getContext().getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            addPermissionData(sysInfoItems);
        } else {
            addPermissionDataUnreliable();
        }
        mInfoItemAdapter.setData(sysInfoItems);
    }

    private void addAppData(List<SysInfoItem> sysInfoItems) {
        PackageInfo pi = DeviceUtils.getPackageInfo(getContext());
        sysInfoItems.add(new TitleItem(getString(R.string.dk_sysinfo_app_info)));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_package_name), pi.packageName));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_package_version_name), pi.versionName));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_package_version_code), String.valueOf(pi.versionCode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_package_min_sdk), String.valueOf(getContext().getApplicationInfo().minSdkVersion)));
        }
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_package_target_sdk), String.valueOf(getContext().getApplicationInfo().targetSdkVersion)));
    }

    private void addDeviceData(List<SysInfoItem> sysInfoItems) {
        sysInfoItems.add(new TitleItem(getString(R.string.dk_sysinfo_device_info)));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_brand_and_model), Build.MANUFACTURER + " " + Build.MODEL));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_android_version), Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")"));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_ext_storage_free), DeviceUtils.getSDCardSpace(getContext())));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_rom_free), DeviceUtils.getRomSpace(getContext())));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_display_size), UIUtils.getWidthPixels() + "x" + UIUtils.getRealHeightPixels()));
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_display_inch), "" + UIUtils.getScreenInch(getActivity())));
        sysInfoItems.add(new SysInfoItem("ROOT", String.valueOf(DeviceUtils.isRoot(getContext()))));
        sysInfoItems.add(new SysInfoItem("DENSITY", String.valueOf(UIUtils.getDensity())));
        sysInfoItems.add(new SysInfoItem("IP", TextUtils.isEmpty(NetworkUtils.getIPAddress(true)) ? "null" : NetworkUtils.getIPAddress(true)));
        sysInfoItems.add(new SysInfoItem("Mac", TextUtils.isEmpty(com.blankj.utilcode.util.DeviceUtils.getMacAddress()) ? "null" : com.blankj.utilcode.util.DeviceUtils.getMacAddress()));
        try {
            sysInfoItems.add(new SysInfoItem("IMEI", TextUtils.isEmpty(PhoneUtils.getIMEI()) ? "null" : PhoneUtils.getIMEI()));
        } catch (Exception e) {
        }
        sysInfoItems.add(new SysInfoItem("Sign MD5", AppUtils.getAppSignatureMD5()));
        sysInfoItems.add(new SysInfoItem("Sign SHA1", AppUtils.getAppSignatureSHA1()));
        sysInfoItems.add(new SysInfoItem("Sign SHA256", AppUtils.getAppSignatureSHA256()));


    }

    /**
     * 不可靠的检测权限方式
     */
    private void addPermissionDataUnreliable() {
        ExecutorUtil.execute(new Runnable() {
            @Override
            public void run() {
                final List<SysInfoItem> list = new ArrayList<>();
                list.add(new TitleItem(getString(R.string.dk_sysinfo_permission_info_unreliable)));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_location), PermissionUtil.checkLocationUnreliable(getContext()) ? "YES" : "NO", true));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_sdcard), PermissionUtil.checkStorageUnreliable() ? "YES" : "NO", true));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_camera), PermissionUtil.checkCameraUnreliable() ? "YES" : "NO", true));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_record), PermissionUtil.checkRecordUnreliable() ? "YES" : "NO", true));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_read_phone), PermissionUtil.checkReadPhoneUnreliable(getContext()) ? "YES" : "NO", true));
                list.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_contact), PermissionUtil.checkReadContactUnreliable(getContext()) ? "YES" : "NO", true));
                getView().post(new Runnable() {
                    @Override
                    public void run() {
                        if (SysInfoFragment.this.isDetached()) {
                            return;
                        }
                        mInfoItemAdapter.append(list);
                    }
                });
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void addPermissionData(List<SysInfoItem> sysInfoItems) {
        sysInfoItems.add(new TitleItem(getString(R.string.dk_sysinfo_permission_info)));
        String[] p1 = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_location), checkPermission(p1), true));
        String[] p2 = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_sdcard), checkPermission(p2), true));
        String[] p3 = {
                Manifest.permission.CAMERA
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_camera), checkPermission(p3), true));
        String[] p4 = {
                Manifest.permission.RECORD_AUDIO
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_record), checkPermission(p4), true));
        String[] p5 = {
                Manifest.permission.READ_PHONE_STATE
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_read_phone), checkPermission(p5), true));
        String[] p6 = {
                Manifest.permission.READ_CONTACTS
        };
        sysInfoItems.add(new SysInfoItem(getString(R.string.dk_sysinfo_permission_contact), checkPermission(p6), true));
    }

    private String checkPermission(String... perms) {
        return PermissionUtil.hasPermissions(getContext(), perms) ? "YES" : "NO";
    }

}

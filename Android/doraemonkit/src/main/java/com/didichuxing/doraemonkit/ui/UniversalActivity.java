package com.didichuxing.doraemonkit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerSettingFragment;
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorFragment;
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickerSettingFragment;
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureMainFragment;
import com.didichuxing.doraemonkit.kit.custom.MonitorDataUploadFragment;
import com.didichuxing.doraemonkit.kit.custom.UploadMonitorInfoBean;
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanFragment;
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerFragment;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockFragment;
import com.didichuxing.doraemonkit.kit.logInfo.LogInfoSettingFragment;
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment;
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuMainPageFragment;
import com.didichuxing.doraemonkit.kit.parameter.frameInfo.FrameInfoFragment;
import com.didichuxing.doraemonkit.kit.parameter.ram.RamMainPageFragment;
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoFragment;
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterFragment;
import com.didichuxing.doraemonkit.kit.viewcheck.ViewCheckFragment;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorDefaultFragment;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorFragment;
import com.didichuxing.doraemonkit.ui.base.BaseActivity;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;

/**
 * Created by wanglikun on 2018/10/26.
 */

public class UniversalActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        int index = bundle.getInt(BundleKey.FRAGMENT_INDEX);
        if (index == 0) {
            finish();
            return;
        }
        Class<? extends BaseFragment> fragmentClass = null;
        switch (index) {
            case FragmentIndex.FRAGMENT_SYS_INFO:
                fragmentClass = SysInfoFragment.class;
                break;
            case FragmentIndex.FRAGMENT_FILE_EXPLORER:
                fragmentClass = FileExplorerFragment.class;
                break;
            case FragmentIndex.FRAGMENT_LOG_INFO_SETTING:
                fragmentClass = LogInfoSettingFragment.class;
                break;
            case FragmentIndex.FRAGMENT_COLOR_PICKER_SETTING:
                fragmentClass = ColorPickerSettingFragment.class;
                break;
            case FragmentIndex.FRAGMENT_FRAME_INFO:
                fragmentClass = FrameInfoFragment.class;
                break;
            case FragmentIndex.FRAGMENT_GPS_MOCK:
                fragmentClass = GpsMockFragment.class;
                break;
            case FragmentIndex.FRAGMENT_ALIGN_RULER_SETTING:
                fragmentClass = AlignRulerSettingFragment.class;
                break;
            case FragmentIndex.FRAGMENT_WEB_DOOR:
                fragmentClass = WebDoorFragment.class;
                break;
            case FragmentIndex.FRAGMENT_DATA_CLEAN:
                fragmentClass = DataCleanFragment.class;
                break;
            case FragmentIndex.FRAGMENT_BLOCK_MONITOR:
                fragmentClass = BlockMonitorFragment.class;
                break;
            case FragmentIndex.FRAGMENT_CRASH:
                fragmentClass = CrashCaptureMainFragment.class;
                break;
            case FragmentIndex.FRAGMENT_VIEW_CHECK:
                fragmentClass = ViewCheckFragment.class;
                break;
            case FragmentIndex.FRAGMENT_NETWORK_MONITOR:
                fragmentClass = NetWorkMonitorFragment.class;
                break;
            case FragmentIndex.FRAGMENT_CPU:
                fragmentClass = CpuMainPageFragment.class;
                break;
            case FragmentIndex.FRAGMENT_RAM:
                fragmentClass = RamMainPageFragment.class;
                break;
            case FragmentIndex.FRAGMENT_TIME_COUNTER:
                fragmentClass = TimeCounterFragment.class;
                break;
            case FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT:
                fragmentClass = WebDoorDefaultFragment.class;
                break;
            case FragmentIndex.FRAGMENT_CUSTOM:
                fragmentClass = MonitorDataUploadFragment.class;
                break;
            default:
                break;
        }
        if (fragmentClass == null) {
            finish();
            Toast.makeText(this, String.format("fragment index %s not found", index), Toast.LENGTH_SHORT).show();
            return;
        }
        showContent(fragmentClass, bundle);
    }
}

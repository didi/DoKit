package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorFragment
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickerSettingFragment
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureMainFragment
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanFragment
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerFragment
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockFragment
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureFragment
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMockFragment
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment
import com.didichuxing.doraemonkit.kit.performance.cpu.CpuInfoPageFragment
import com.didichuxing.doraemonkit.kit.performance.fps.FrameInfoPageFragment
import com.didichuxing.doraemonkit.kit.performance.ram.RamInfoPageFragment
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoFragment
import com.didichuxing.doraemonkit.kit.timecounter.AppRecordFragment
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterFragment
import com.didichuxing.doraemonkit.kit.toolpanel.DokitManagerFragment
import com.didichuxing.doraemonkit.kit.toolpanel.DokitSettingFragment
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkFragment
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorDefaultFragment
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorFragment


/**
 * Created by wanglikun on 2018/10/26.
 * app基础信息Activity
 */
open class UniversalActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle == null) {
            finish()
            return
        }
        val index = bundle.getInt(BundleKey.FRAGMENT_INDEX)
        if (index == 0) {
            finish()
            return
        }
        val fragmentClass: Class<out BaseFragment?>? = when (index) {
            FragmentIndex.FRAGMENT_DOKIT_SETTING -> DokitSettingFragment::class.java
            FragmentIndex.FRAGMENT_DOKIT_MANAGER -> DokitManagerFragment::class.java
            FragmentIndex.FRAGMENT_WEAK_NETWORK -> WeakNetworkFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR -> WebDoorFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT -> WebDoorDefaultFragment::class.java
            FragmentIndex.FRAGMENT_COLOR_PICKER_SETTING -> ColorPickerSettingFragment::class.java
            FragmentIndex.FRAGMENT_FRAME_INFO -> FrameInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_CPU -> CpuInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_RAM -> RamInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_TIME_COUNTER -> TimeCounterFragment::class.java
            FragmentIndex.FRAGMENT_APP_RECORD_DETAIL -> AppRecordFragment::class.java
            FragmentIndex.FRAGMENT_SYS_INFO -> SysInfoFragment::class.java
            FragmentIndex.FRAGMENT_LARGE_PICTURE -> LargePictureFragment::class.java
            // 性能监控-> 卡顿检测
            FragmentIndex.FRAGMENT_BLOCK_MONITOR -> BlockMonitorFragment::class.java
            FragmentIndex.FRAGMENT_DATA_CLEAN -> DataCleanFragment::class.java
            FragmentIndex.FRAGMENT_NETWORK_MONITOR -> NetWorkMonitorFragment::class.java
            FragmentIndex.FRAGMENT_NETWORK_MOCK -> NetWorkMockFragment::class.java
            FragmentIndex.FRAGMENT_FILE_EXPLORER -> FileExplorerFragment::class.java
            FragmentIndex.FRAGMENT_GPS_MOCK -> GpsMockFragment::class.java
            FragmentIndex.FRAGMENT_CRASH -> CrashCaptureMainFragment::class.java
            else -> null
        }
        if (fragmentClass == null) {
            finish()
            ToastUtils.showShort(String.format("fragment index %s not found", index), Toast.LENGTH_SHORT)
            return
        }
        showContent(fragmentClass, bundle)
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import android.widget.Toast
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerSettingFragment
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorFragment
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickerSettingFragment
import com.didichuxing.doraemonkit.kit.crash.CrashCaptureMainFragment
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanFragment
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerFragment
import com.didichuxing.doraemonkit.kit.filemanager.FileTransferFragment
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockFragment
import com.didichuxing.doraemonkit.kit.health.HealthFragment
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureFragment
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoSettingFragment
import com.didichuxing.doraemonkit.kit.network.ui.MockTemplatePreviewFragment
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMockFragment
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuMainPageFragment
import com.didichuxing.doraemonkit.kit.parameter.frameInfo.FrameInfoFragment
import com.didichuxing.doraemonkit.kit.parameter.ram.RamMainPageFragment
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoFragment
import com.didichuxing.doraemonkit.kit.sysinfo.ThirdLibInfoFragment
import com.didichuxing.doraemonkit.kit.timecounter.AppStartInfoFragment
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterFragment
import com.didichuxing.doraemonkit.kit.toolpanel.DokitManagerFragment
import com.didichuxing.doraemonkit.kit.toolpanel.DokitMoreFragment
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkFragment
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorDefaultFragment
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorFragment
import com.didichuxing.doraemonkit.kit.webview.CommWebViewFragment

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
        var fragmentClass: Class<out BaseFragment?>? = null
        when (index) {
            FragmentIndex.FRAGMENT_SYS_INFO -> fragmentClass = SysInfoFragment::class.java
            FragmentIndex.FRAGMENT_THIRD_LIBRARY_INFO -> fragmentClass = ThirdLibInfoFragment::class.java
            FragmentIndex.FRAGMENT_FILE_EXPLORER -> fragmentClass = FileExplorerFragment::class.java
            FragmentIndex.FRAGMENT_LOG_INFO_SETTING -> fragmentClass = LogInfoSettingFragment::class.java
            FragmentIndex.FRAGMENT_COLOR_PICKER_SETTING -> fragmentClass = ColorPickerSettingFragment::class.java
            FragmentIndex.FRAGMENT_GPS_MOCK -> fragmentClass = GpsMockFragment::class.java
            FragmentIndex.FRAGMENT_ALIGN_RULER_SETTING -> fragmentClass = AlignRulerSettingFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR -> fragmentClass = WebDoorFragment::class.java
            FragmentIndex.FRAGMENT_DATA_CLEAN -> fragmentClass = DataCleanFragment::class.java
            FragmentIndex.FRAGMENT_WEAK_NETWORK -> fragmentClass = WeakNetworkFragment::class.java
            FragmentIndex.FRAGMENT_FRAME_INFO -> fragmentClass = FrameInfoFragment::class.java
            FragmentIndex.FRAGMENT_BLOCK_MONITOR -> fragmentClass = BlockMonitorFragment::class.java
            FragmentIndex.FRAGMENT_CRASH -> fragmentClass = CrashCaptureMainFragment::class.java
            FragmentIndex.FRAGMENT_NETWORK_MONITOR -> fragmentClass = NetWorkMonitorFragment::class.java
            FragmentIndex.FRAGMENT_CPU -> fragmentClass = CpuMainPageFragment::class.java
            FragmentIndex.FRAGMENT_RAM -> fragmentClass = RamMainPageFragment::class.java
            FragmentIndex.FRAGMENT_TIME_COUNTER -> fragmentClass = TimeCounterFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT -> fragmentClass = WebDoorDefaultFragment::class.java
            FragmentIndex.FRAGMENT_LARGE_PICTURE -> fragmentClass = LargePictureFragment::class.java
            FragmentIndex.FRAGMENT_WEB -> fragmentClass = CommWebViewFragment::class.java
            FragmentIndex.FRAGMENT_NETWORK_MOCK -> fragmentClass = NetWorkMockFragment::class.java
            FragmentIndex.FRAGMENT_MOCK_TEMPLATE_PREVIEW -> fragmentClass = MockTemplatePreviewFragment::class.java
            FragmentIndex.FRAGMENT_HEALTH -> fragmentClass = HealthFragment::class.java
            FragmentIndex.FRAGMENT_FILE_TRANSFER -> fragmentClass = FileTransferFragment::class.java
            //FragmentIndex.FRAGMENT_FILE_TRANSFER_DOC -> fragmentClass = FileTransformDocFragment::class.java
            FragmentIndex.FRAGMENT_APP_START -> fragmentClass = AppStartInfoFragment::class.java
            FragmentIndex.FRAGMENT_DOKIT_MORE -> fragmentClass = DokitMoreFragment::class.java
            FragmentIndex.FRAGMENT_DOKIT_MANAGER -> fragmentClass = DokitManagerFragment::class.java
            else -> {
            }
        }
        if (fragmentClass == null) {
            finish()
            Toast.makeText(this, String.format("fragment index %s not found", index), Toast.LENGTH_SHORT).show()
            return
        }
        showContent(fragmentClass, bundle)
    }
}
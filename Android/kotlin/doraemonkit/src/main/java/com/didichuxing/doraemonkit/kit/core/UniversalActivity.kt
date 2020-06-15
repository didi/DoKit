package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorFragment
import com.didichuxing.doraemonkit.kit.dataclean.DataCleanFragment
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerFragment
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureFragment
import com.didichuxing.doraemonkit.kit.performance.cpu.CpuInfoPageFragment
import com.didichuxing.doraemonkit.kit.performance.fps.FrameInfoPageFragment
import com.didichuxing.doraemonkit.kit.performance.ram.RamInfoPageFragment
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoFragment
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
        var fragmentClass: Class<out BaseFragment?>? = null
        when (index) {
            FragmentIndex.FRAGMENT_DOKIT_SETTING -> fragmentClass = DokitSettingFragment::class.java
            FragmentIndex.FRAGMENT_DOKIT_MANAGER -> fragmentClass = DokitManagerFragment::class.java
            FragmentIndex.FRAGMENT_WEAK_NETWORK -> fragmentClass = WeakNetworkFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR -> fragmentClass = WebDoorFragment::class.java
            FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT -> fragmentClass = WebDoorDefaultFragment::class.java
            FragmentIndex.FRAGMENT_FRAME_INFO -> fragmentClass = FrameInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_CPU -> fragmentClass = CpuInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_RAM -> fragmentClass = RamInfoPageFragment::class.java
            FragmentIndex.FRAGMENT_SYS_INFO -> fragmentClass = SysInfoFragment::class.java
            FragmentIndex.FRAGMENT_LARGE_PICTURE -> fragmentClass = LargePictureFragment::class.java
            // 性能监控-> 卡顿检测
            FragmentIndex.FRAGMENT_BLOCK_MONITOR -> fragmentClass = BlockMonitorFragment::class.java
            FragmentIndex.FRAGMENT_DATA_CLEAN -> fragmentClass = DataCleanFragment::class.java
            FragmentIndex.FRAGMENT_FILE_EXPLORER -> fragmentClass = FileExplorerFragment::class.java
            else -> {
            }
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
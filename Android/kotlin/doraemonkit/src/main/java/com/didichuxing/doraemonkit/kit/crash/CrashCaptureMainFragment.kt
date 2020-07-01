package com.didichuxing.doraemonkit.kit.crash

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.CrashCaptureConfig
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.kit.fileexplorer.FileExplorerFragment
import com.didichuxing.doraemonkit.util.FileUtil
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * Created by wangxueying on 2020-06-30
 */
class CrashCaptureMainFragment : BaseFragment() {
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_crash_capture_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }

    private fun initview() {
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        })
        val settingList = findViewById<RecyclerView>(R.id.setting_list)
        settingList.layoutManager = LinearLayoutManager(context)
        val mSettingItemAdapter = SettingItemAdapter(context)
        mSettingItemAdapter.append(
                SettingItem(R.string.dk_crash_capture_switch, CrashCaptureConfig.isCrashCaptureOpen))
        mSettingItemAdapter.append(SettingItem(R.string.dk_crash_capture_look, R.mipmap.dk_more_icon, false))
        val item = SettingItem(R.string.dk_crash_capture_clean_data)
        item.rightDesc = Formatter.formatFileSize(context, FileUtil
                .getDirectorySize(CrashCaptureManager.instance.crashCacheDir))
        mSettingItemAdapter.append(item)
        mSettingItemAdapter.setOnSettingItemSwitchListener(object : SettingItemAdapter.OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (data.desc == R.string.dk_crash_capture_switch) {
                    CrashCaptureConfig.isCrashCaptureOpen = on
                    if (on) {
                        CrashCaptureManager.instance.start()
                    } else {
                        CrashCaptureManager.instance.stop()
                    }
                }
            }
        })
        mSettingItemAdapter.setOnSettingItemClickListener(object : SettingItemAdapter.OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data.desc == R.string.dk_crash_capture_look) {
                    val bundle = Bundle()
                    bundle.putSerializable(BundleKey.DIR_KEY, CrashCaptureManager.instance.crashCacheDir)
                    showContent(FileExplorerFragment::class.java, bundle)
                } else if (data.desc == R.string.dk_crash_capture_clean_data) {
                    CrashCaptureManager.instance.clearCacheHistory()
                    data.rightDesc = Formatter.formatFileSize(context, FileUtil
                            .getDirectorySize(CrashCaptureManager.instance.crashCacheDir))
                    mSettingItemAdapter.notifyDataSetChanged()
                    showToast(R.string.dk_crash_capture_clean_data)
                }
            }
        })
        settingList.adapter = mSettingItemAdapter
    }
}

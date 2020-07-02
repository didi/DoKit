package com.didichuxing.doraemonkit.kit.timecounter

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.*
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.method_stack.MethodStackUtil
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.util.FileUtil.systemShare
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.io.File

/**
 * @desc: App启动耗时记录
 *
 * @author yfengtech
 * 2020-07-02 18:09:27
 */
class AppRecordFragment : BaseFragment() {
    private lateinit var mInfo: TextView
    override fun onRequestLayout(): Int = R.layout.dk_fragment_app_record

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mInfo = findViewById(R.id.app_start_info)

        val titleBar = findViewById<TitleBar>(R.id.title_bar)
        titleBar.onTitleBarClickListener =
                object : TitleBar.OnTitleBarClickListener {
                    override fun onLeftClick() {
                        finish()
                    }

                    override fun onRightClick() {
                        export2File(mInfo.text.toString())
                    }
                }

        val builder = StringBuilder()
        if (TextUtils.isEmpty(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT)) {
            builder.append("只有配置slowMethod的strategy=0模式下才能统计到启动函数调用栈")
        } else {
            builder.append(MethodStackUtil.STR_APP_ATTACH_BASECONTEXT)
            builder.append("\n")
            builder.append(MethodStackUtil.STR_APP_ON_CREATE)
        }
        mInfo.text = builder.toString()

    }

    /**
     * 将启动信息保存到文件并分享
     */
    private fun export2File(info: String) {
        if (TextUtils.isEmpty(info)) {
            ToastUtils.showShort("启动信息为空")
            return
        }
        ToastUtils.showShort("启动信息保存中,请稍后...")
        val logPath =
                PathUtils.getInternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_" + "app_launch.log"
        val logFile = File(logPath)
        ThreadUtils.executeByCpu(object : ThreadUtils.Task<Boolean>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Boolean {
                return try {
                    FileIOUtils.writeFileFromString(logFile, info, false)
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            override fun onSuccess(result: Boolean) {
                if (result) {
                    ToastUtils.showShort("启动信息文件保存在:$logPath")
                    //分享
                    DoraemonKit.APPLICATION?.let {
                        systemShare(it, logFile)
                    }
                }
            }

            override fun onCancel() {
                if (logFile.exists()) {
                    FileUtils.delete(logFile)
                }
                ToastUtils.showShort("启动信息保存失败")
            }

            override fun onFail(t: Throwable) {
                if (logFile.exists()) {
                    FileUtils.delete(logFile)
                }
                ToastUtils.showShort("启动信息保存失败")
            }
        })
    }
}
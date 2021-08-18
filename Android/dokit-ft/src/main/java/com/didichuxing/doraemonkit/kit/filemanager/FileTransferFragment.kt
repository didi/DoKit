package com.didichuxing.doraemonkit.kit.filemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.ft.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.webview.CommWebViewFragment
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import java.net.BindException

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-13:45
 * 描    述：文件互传fragment
 * 修订历史：
 * ================================================
 */
class FileTransferFragment : BaseFragment() {
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_file_transfer
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewById<TextView>(R.id.tv_ip).text =
            "${DoKitManager.IP_ADDRESS_BY_WIFI}:${DoKitManager.FILE_MANAGER_HTTP_PORT}"
        findViewById<HomeTitleBar>(R.id.title_bar).setListener { finish() }
        findViewById<TextView>(R.id.tv_tip_top).apply {
            text = Html.fromHtml(DoKitCommUtil.getString(R.string.dk_file_manager_tip_top))
            setOnClickListener {
                DoKit.launchFullScreen(
                    CommWebViewFragment::class,
                    context,
                    isSystemFragment = true
                )
            }
        }

        initKtor()
    }

    private fun initKtor() {
        try {
            HttpServer.server.start()
        } catch (e: BindException) {

        }
    }
}
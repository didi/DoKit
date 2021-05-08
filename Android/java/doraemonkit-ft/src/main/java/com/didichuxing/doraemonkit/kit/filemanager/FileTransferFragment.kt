package com.didichuxing.doraemonkit.kit.filemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.webview.CommWebViewFragment
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import kotlinx.android.synthetic.main.dk_fragment_file_transfer.*
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
        tv_ip.text = "${DoKitConstant.IP_ADDRESS_BY_WIFI}:${DoKitConstant.FILE_MANAGER_HTTP_PORT}"
        title_bar.setListener { finish() }
        tv_tip_top.text = Html.fromHtml(DoKitCommUtil.getString(R.string.dk_file_manager_tip_top))
        tv_tip_top.setOnClickListener {
            SimpleDokitStarter.startFullScreen(
                CommWebViewFragment::class.java,
                context,
                isSystemFragment = true
            )
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
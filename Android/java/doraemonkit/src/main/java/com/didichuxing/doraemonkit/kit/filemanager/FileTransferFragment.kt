package com.didichuxing.doraemonkit.kit.filemanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.webview.WebViewManager
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import kotlinx.android.synthetic.main.dk_fragment_db_debug.title_bar
import kotlinx.android.synthetic.main.dk_fragment_db_debug.tv_ip
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
            WebViewManager.url = NetworkManager.FILE_MANAGER_DOCUMENT_URL
            val intent = Intent(context, UniversalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_WEB)
            startActivity(intent)
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
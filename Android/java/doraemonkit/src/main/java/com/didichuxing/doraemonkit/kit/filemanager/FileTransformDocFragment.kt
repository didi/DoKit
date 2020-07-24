package com.didichuxing.doraemonkit.kit.filemanager

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.widget.webview.MyWebView

/**
 * @author jintai
 * @desc: dokit 文件同步助手文档
 */
class FileTransformDocFragment : BaseFragment() {
    private var mWebView: MyWebView? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_file_manager_doc
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val title = findViewById<HomeTitleBar>(R.id.title_bar)
        title.setListener { finish() }
        mWebView = findViewById(R.id.webview)
        mWebView?.loadUrl(NetworkManager.FILE_MANAGER_DOCUMENT_URL)
    }


}
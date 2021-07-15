package com.didichuxing.doraemonkit.kit.webview

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.widget.webview.MyWebView

/**
 * @author jintai
 * @desc: 全局webview fragment
 */
class CommWebViewFragment : BaseFragment() {
    private lateinit var mWebView: MyWebView
    private lateinit var mTitle: HomeTitleBar
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_comm_webview
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        mTitle = findViewById(R.id.title_bar)
        mTitle.setListener {
            activity?.finish()
        }
        mWebView = findViewById(R.id.webview)
        WebViewManager.url?.let { url ->
            mWebView.loadUrl(url)
            mWebView.setCallBack { title ->
                title?.let {
                    mTitle.setTitle(it)
                }
            }
        }
    }

}
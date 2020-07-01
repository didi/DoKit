package com.didichuxing.doraemonkit.kit.filemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.http.HttpServer
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
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
        tv.text = "${NetworkUtils.getIpAddressByWifi()}:${DokitConstant.FILE_MANAGER_HTTP_PORT}"
        initKtor()
    }

    private fun initKtor() {
        try {
            HttpServer.server.start()
        } catch (e: BindException) {

        }

    }
}
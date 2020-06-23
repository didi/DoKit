package com.didichuxing.doraemonkit.kit.filetransfer

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import io.ktor.http.headersOf
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initKtor()
    }

    private fun initKtor() {
        val server = embeddedServer(CIO, port = 8089, module = DoKitFileRouter)
        server.start()
    }
}
package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.all.DoKitWindowManager
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import com.didichuxing.doraemonkit.kit.mc.server.HostInfo
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.kit.mc.util.McUtil
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcDatasFragment : BaseFragment() {
    lateinit var mRv: RecyclerView
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_datas
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRv = findViewById(R.id.rv)

    }


}
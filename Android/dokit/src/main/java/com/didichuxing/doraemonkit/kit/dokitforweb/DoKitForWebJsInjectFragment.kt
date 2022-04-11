package com.didichuxing.doraemonkit.kit.dokitforweb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitForWebJsInjectFragment : BaseFragment() {

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_dokit_for_web
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeTitleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        val editText = findViewById<EditText>(R.id.et_h5_url)
        val h5_file = findViewById<RadioButton>(R.id.h5_file)
        val h5_url = findViewById<RadioButton>(R.id.h5_url)
        val tv_h5jsInject = findViewById<RadioGroup>(R.id.tv_h5jsInject)
        val switch_btn = findViewById<Switch>(R.id.switch_btn)

        homeTitleBar.setListener { activity?.finish() }

        if (DoKitManager.H5_MC_JS_INJECT_MODE == "file") {
            h5_file.isChecked = true
        } else {
            h5_url.isChecked = true
        }

        val isOk = findViewById<Button>(R.id.btn_ok)
        isOk.setOnClickListener {
            DokitForWeb.saveMcH5Inject(switch_btn.isChecked)
            DokitForWeb.saveMcInjectUrl(editText.text.toString())
            if (h5_url.isChecked) {
                DokitForWeb.saveMcInjectMode("url")
            } else {
                DokitForWeb.saveMcInjectMode("file")
            }

            ToastUtils.showShort("保存成功")
            activity?.finish()
        }
        editText.setText(DoKitManager.H5_MC_JS_INJECT_URL)
        switch_btn.isChecked = DoKitManager.H5_DOKIT_MC_INJECT

    }


}

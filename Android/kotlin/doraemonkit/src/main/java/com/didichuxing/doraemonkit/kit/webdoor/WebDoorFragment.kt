package com.didichuxing.doraemonkit.kit.webdoor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.zxing.activity.ZXingCodeCaptureActivity

/**
 * Created by guofeng007 on 2020/6/8
 */
class WebDoorFragment : BaseFragment() {
    private lateinit var mWebAddressInput: EditText
    private lateinit var mUrlExplore: TextView
    private lateinit var mHistoryList: RecyclerView
    private lateinit var mWebDoorHistoryAdapter: WebDoorHistoryAdapter
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_web_door
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.mListener = object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        }
        mWebAddressInput = findViewById(R.id.web_address_input)
        mUrlExplore = findViewById(R.id.url_explore)
        mWebAddressInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mUrlExplore.isEnabled = checkInput()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        findViewById<View>(R.id.clear).setOnClickListener {
            WebDoorManager.instance.clearHistory()
            mHistoryList.adapter = null
        }
        findViewById<View>(R.id.qr_code).setOnClickListener { qrCode() }
        mUrlExplore.setOnClickListener(View.OnClickListener { doSearch(mWebAddressInput.text.toString()) })
        mHistoryList = findViewById(R.id.history_list)
        mHistoryList.isNestedScrollingEnabled = false
        mHistoryList.layoutManager = LinearLayoutManager(context)
        val historyItems: List<String>? = WebDoorManager.instance.history
        mWebDoorHistoryAdapter = WebDoorHistoryAdapter(R.layout.dk_item_web_door_history, historyItems?.toMutableList())
        mWebDoorHistoryAdapter.setOnItemClickListener { adapter, view, position ->
            var url: String? = historyItems?.get(position)
            doSearch(url)
        }
        mHistoryList.adapter = mWebDoorHistoryAdapter
    }

    private fun doSearch(url: String?) {
        if (WebDoorManager.instance.saveHistory(url!!)) {
            mWebDoorHistoryAdapter.addData(url)
        }


        WebDoorManager.instance.webDoorCallback.invoke(context!!, url)


    }

    private fun checkInput(): Boolean {
        return !TextUtils.isEmpty(mWebAddressInput.text)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_QR_CODE) {
            val bundle = data?.extras
            val result = bundle!!.getString(ZXingCodeCaptureActivity.INTENT_EXTRA_KEY_QR_SCAN)

            if (!TextUtils.isEmpty(result)) {
                doSearch(result)
            } else {
                showToast("scan failed")
            }
        }
    }


    private fun qrCode() {
        if (!ownPermissionCheck()) {
            requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA)
            return
        }
        val intent = Intent(activity, ZXingCodeCaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_QR_CODE)
    }

    private fun ownPermissionCheck(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA) {
            for (grantResult in grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    qrCode()
                } else {
                    showToast("need camera permission")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val REQUEST_CAMERA = 2
        private const val REQUEST_QR_CODE = 3
        private val PERMISSIONS_CAMERA = arrayOf(
                Manifest.permission.CAMERA)
    }
}
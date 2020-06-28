package com.didichuxing.doraemonkit.kit.gpsmock

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.GpsMockConfig
import com.didichuxing.doraemonkit.config.GpsMockConfig.setGPSMockOpen
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.model.LatLng
import com.didichuxing.doraemonkit.util.WebViewUtil
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import com.didichuxing.doraemonkit.widget.webview.MyWebView
import com.didichuxing.doraemonkit.widget.webview.MyWebViewClient

/**
 * @author lostjobs created on 2020/6/27
 */
class GpsMockFragment : BaseFragment(), MyWebViewClient.InvokeListener, SettingItemAdapter.OnSettingItemSwitchListener {

    companion object {
        private const val TAG = "GpsMockFragment"
    }

    private lateinit var mSettingList: RecyclerView
    private val mSettingAdapter by lazy { SettingItemAdapter(context) }

    private lateinit var mWebView: MyWebView

    private var isInit: Boolean = true
    private lateinit var mEdLongLat: TextView
    private lateinit var mIvSearch: ImageView

    override fun onRequestLayout(): Int = R.layout.dk_fragment_gps_mock

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSettingList()
        initTitleBar()
        initMockLocationArea()
        initMapWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.removeInvokeListener(this)
    }

    private fun initSettingList() {
        mSettingList = findViewById(R.id.setting_list)
        val listItems = mutableListOf<SettingItem>()
        listItems.add(SettingItem(R.string.dk_gpsmock_open, GpsMockConfig.isGPSMockOpen()))
        mSettingAdapter.setNewInstance(listItems)
        mSettingAdapter.setOnSettingItemSwitchListener(this)
        with(mSettingList) {
            layoutManager = LinearLayoutManager(context)
            adapter = mSettingAdapter
            addItemDecoration(DividerItemDecoration(DividerItemDecoration.VERTICAL).also { it.setDrawable(ContextCompat.getDrawable(context, R.drawable.dk_divider)) })
        }
    }

    private fun initTitleBar() {
        findViewById<HomeTitleBar>(R.id.title_bar).setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                finish()
            }
        })
    }

    private fun initMockLocationArea() {
        mEdLongLat = findViewById(R.id.ed_long_lat)
        mIvSearch = findViewById(R.id.iv_search)

        mIvSearch.setOnClickListener {
            val latLng = getLatLngFrom(mEdLongLat) ?: return@setOnClickListener
            GpsMockManager.instance.mockLocation(latLng.latitude, latLng.longitude)
            GpsMockConfig.saveMockLocation(latLng = latLng)

            //刷新地图
            val url = String.format("javascript:updateLocation(%s,%s)", latLng.latitude, latLng.longitude)
            mWebView.loadUrl(url)
            ToastUtils.showShort(getString(R.string.dk_gps_location_change_toast, "" + latLng.longitude, "" + latLng.latitude))
        }
    }

    private fun initMapWebView() {
        mWebView = findViewById(R.id.web_view)
        WebViewUtil.webVIewLoadLocalHtml(mWebView, "html/map.html")
        mWebView.addInvokeListener(this)
    }

    override fun onNativeInvoke(url: String?) {
        if (url.isNullOrEmpty()) return
        val uri = Uri.parse(url)
        if (uri.lastPathSegment != "sendLocation") return
        val lat = uri.getQueryParameter("lat")
        val lng = uri.getQueryParameter("lng")
        if (lat.isNullOrEmpty() || lng.isNullOrEmpty()) return
        mEdLongLat.text = String.format("%s %s", lng, lat)

        if (!isInit) {
            val longitude = lat.toDoubleOrNull()
            val latitude = lng.toDoubleOrNull()
            if (null == longitude || null == latitude) {
                ToastUtils.showShort("经纬度必须为数字")
                return
            }
            GpsMockManager.instance.mockLocation(latitude, longitude)
            GpsMockConfig.saveMockLocation(LatLng(latitude, longitude))
            ToastUtils.showShort(getString(R.string.dk_gps_location_change_toast, "" + longitude, "" + latitude))
        }
        isInit = false
    }

    private fun getLatLngFrom(textView: TextView): LatLng? {
        val text = textView.text.toString()
        if (text.isEmpty()) {
            ToastUtils.showShort("请输入经纬度")
            return null
        }
        val longAndLat = text.split(" ")
        if (longAndLat.size != 2) {
            ToastUtils.showShort("请输入符合规范的经纬度格式")
            return null
        }
        val longitude = longAndLat[0]
        val latitude = longAndLat[1]
        if (!checkLocationIllegal(longitude, -180.0, 180.0) || !checkLocationIllegal(latitude, -90.0, 90.0)) {
            ToastUtils.showShort("请输入符合规范的经纬度格式")
            return null
        }
        return LatLng(longitude.toDouble(), latitude.toDouble())
    }

    private fun checkLocationIllegal(text: String, min: Double, max: Double): Boolean {
        val location = text.toDoubleOrNull() ?: return false
        return location in min..max
    }

    override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
        if (data.desc == R.string.dk_gpsmock_open) {
            if (on) {
                GpsMockManager.instance.startMock()
            } else {
                GpsMockManager.instance.stopMock()
            }
            setGPSMockOpen(on)
        }
    }
}
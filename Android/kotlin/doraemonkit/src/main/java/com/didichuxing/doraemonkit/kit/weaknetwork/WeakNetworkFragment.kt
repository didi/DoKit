package com.didichuxing.doraemonkit.kit.weaknetwork

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.*
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import kotlinx.android.synthetic.main.dk_fragment_weak_network.*
import java.lang.String

class WeakNetworkFragment : BaseFragment(), TextWatcher {

    private var mNetWorkDokitView: AbsDokitView? = null

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_weak_network
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        title_bar.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                activity?.finish()
            }
        })

        val settingItemAdapter = SettingItemAdapter(context)
        settingItemAdapter.append(SettingItem(R.string.dk_weak_network_switch, 0, isChecked = WeakNetworkManager.get().isActive))

        settingItemAdapter.setOnSettingItemSwitchListener(object : SettingItemAdapter.OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (data.desc == R.string.dk_weak_network_switch) {
                    setWeakNetworkEnabled(data.isChecked)
                }
            }
        })
        setting_list.layoutManager = LinearLayoutManager(context)
        setting_list.adapter = settingItemAdapter

        weak_network_option.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.timeout == checkedId) {
                //超时
                showTimeoutOptionView()
            } else if (R.id.speed_limit == checkedId) {
                //限速
                showSpeedLimitOptionView()
            } else {
                //断网
                showOffNetworkOptionView()
            }
            mNetWorkDokitView = mNetWorkDokitView
                    ?: DokitViewManager.instance.getDokitView(activity, NetWokDokitView::class.java.simpleName)
            //重新调用刷新
            mNetWorkDokitView?.onResume()
        }

        value_timeout.addTextChangedListener(this)
        request_speed.addTextChangedListener(this)
        response_speed.addTextChangedListener(this)
        updateUIState()
    }

    private fun updateUIState() {
        val active: Boolean = WeakNetworkManager.get().isActive
        weak_network_layout.visibility = if (active) View.VISIBLE else View.GONE
        if (active) {
            val checkButtonId: Int
            val type: Int = WeakNetworkManager.get().type
            checkButtonId = when (type) {
                WeakNetworkManager.TYPE_TIMEOUT -> R.id.timeout
                WeakNetworkManager.TYPE_SPEED_LIMIT -> R.id.speed_limit
                else -> R.id.off_network
            }
            val defaultOptionView = findViewById<RadioButton>(checkButtonId)
            defaultOptionView.isChecked = true
            value_timeout.hint = String.valueOf(WeakNetworkManager.get().timeOutMillis)
            request_speed.hint = String.valueOf(WeakNetworkManager.get().requestSpeed)
            response_speed.hint = String.valueOf(WeakNetworkManager.get().responseSpeed)
        }
    }

    private fun setWeakNetworkEnabled(enabled: Boolean) {
        WeakNetworkManager.get().isActive = enabled
        updateUIState()
        if (enabled) {
            val dokitIntent = DokitIntent(NetWokDokitView::class.java)
            dokitIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
            DokitViewManager.instance.attach(dokitIntent)
            mNetWorkDokitView = DokitViewManager.instance.getDokitView(activity, NetWokDokitView::class.java.simpleName)
        } else {
            DokitViewManager.instance.detach(NetWokDokitView::class.java)
        }
    }

    private fun showTimeoutOptionView() {
        layout_timeout_option.visibility = View.VISIBLE
        layout_speed_limit.visibility = View.GONE
        WeakNetworkManager.get().type = WeakNetworkManager.TYPE_TIMEOUT
    }

    private fun showSpeedLimitOptionView() {
        layout_speed_limit.visibility = View.VISIBLE
        layout_timeout_option.visibility = View.GONE
        WeakNetworkManager.get().type = WeakNetworkManager.TYPE_SPEED_LIMIT
    }

    private fun showOffNetworkOptionView() {
        layout_speed_limit.visibility = View.GONE
        layout_timeout_option.visibility = View.GONE
        WeakNetworkManager.get().type = WeakNetworkManager.TYPE_OFF_NETWORK
    }

    private fun getLongValue(editText: EditText): Long {
        val text: CharSequence = editText.text
        return if (TextUtils.isEmpty(text)) 0L else text.toString().toLong()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val timeOutMillis = getLongValue(value_timeout)
        val requestSpeed = getLongValue(request_speed)
        val responseSpeed = getLongValue(response_speed)
        WeakNetworkManager.get().setParameter(timeOutMillis, requestSpeed, responseSpeed)
    }

    override fun afterTextChanged(s: Editable?) {}
}
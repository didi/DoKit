package com.didichuxing.doraemonkit.kit.largepicture

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.getLargeImgFileThreshold
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.getLargeImgMemoryThreshold
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.isLargeImgOpen
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.setLargeImgFileThreshold
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.setLargeImgMemoryThreshold
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.setLargeImgOpen
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureItemAdapter.OnSettingItemClickListener
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureItemAdapter.OnSettingItemSwitchListener
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import java.text.DecimalFormat

/**
 * ================================================
 * 作    者：maple(王枫)
 * 版    本：1.0
 * 创建日期：2020/6/08-12:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LargePictureFragment : BaseFragment() {
    private lateinit var mSettingItemAdapter: LargePictureItemAdapter
    private lateinit var mSettingList: RecyclerView
    private val mDecimalFormat = DecimalFormat("0.00")
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_performance_large_picture_setting;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val titleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        titleBar.setTitle(R.string.dk_category_large_image)
        //TextView tvDesc = findViewById(R.id.tv_desc);
        //tvDesc.setText(Html.fromHtml(getResources().getString(R.string.dk_large_picture_threshold_desc)));
        val fileEditText = findViewById<EditText>(R.id.ed_file_threshold)
        fileEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        ToastUtils.showShort("value can not null")
                        return
                    }
                    if (s.toString().toFloat() < 0) {
                        ToastUtils.showShort("value can not  < 0")
                        return
                    }
                    val value = s.toString().toFloat()
                    val formateValue = mDecimalFormat.format(value.toDouble()).toFloat()
                    //设置文件大小
                    setLargeImgFileThreshold(formateValue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        val memoryEditText = findViewById<EditText>(R.id.ed_memory_threshold)
        memoryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        ToastUtils.showShort("value can not null")
                        return
                    }
                    if (s.toString().toFloat() < 0) {
                        ToastUtils.showShort("value can not  < 0")
                        return
                    }
                    val value = s.toString().toFloat()
                    val formateValue = mDecimalFormat.format(value.toDouble()).toFloat()
                    //设置内存大小
                    setLargeImgMemoryThreshold(formateValue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        val fileThreshold = getLargeImgFileThreshold(LargePictureManager.FILE_DEFAULT_THRESHOLD)
        fileEditText.setText(mDecimalFormat.format(fileThreshold))
        val memoryThreshold = getLargeImgMemoryThreshold(LargePictureManager.MEMORY_DEFAULT_THRESHOLD)
        memoryEditText.setText(mDecimalFormat.format(memoryThreshold))
        titleBar.mListener = (object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                activity!!.finish()
            }
        })
        mSettingList = findViewById(R.id.setting_list)
        mSettingList.layoutManager = LinearLayoutManager(context)
        mSettingItemAdapter = LargePictureItemAdapter()
        mSettingItemAdapter.addData(SettingItem(R.string.dk_large_picture_switch, 0,true, PerformanceSpInfoConfig.isLargeImgOpen()))
        mSettingItemAdapter.addData(SettingItem(R.string.dk_large_picture_look, R.mipmap.dk_more_icon,false))
        mSettingItemAdapter.setOnSettingItemSwitchListener(object : OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View?, data: SettingItem?, on: Boolean) {
                if (data!!.desc == R.string.dk_large_picture_switch) {
                    setLargeImgOpen(on)
                    if (on) {

                        //todo:等待network完成.
                        /* if (!NetworkManager.isActive()) {
                             NetworkManager.get().startMonitor()
                         }*/
                    } else {
                        //todo:等待network完成.
                        // NetworkManager.get().stopMonitor()
                        //清空缓存
                        LargePictureManager.LARGE_IMAGE_INFO_MAP.clear()
                    }
                }
            }
        })
        mSettingItemAdapter.setOnSettingItemClickListener(object : OnSettingItemClickListener {
            override fun onSettingItemClick(view: View?, data: SettingItem?) {
                if (data!!.desc == R.string.dk_large_picture_look) {
                     //测试列表正常的测试数据
                    // addTestData()
                    showContent(LargeImageListFragment::class.java)
                }
            }


        })
        mSettingList.adapter = mSettingItemAdapter
    }
    private fun addTestData() {
        val info=LargeImageInfo()
        info.setUrl("testurl")
        info.setFileSize(100.0)
        info.setFramework("Glide")
        info.setHeight(200)
        info.setMemorySize(100.0)
        info.setWidth(200)
        LargePictureManager.LARGE_IMAGE_INFO_MAP[info.getUrl()?:"null"] = info;
    }
}
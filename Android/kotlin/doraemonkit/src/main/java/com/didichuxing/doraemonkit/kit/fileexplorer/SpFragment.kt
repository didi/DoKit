package com.didichuxing.doraemonkit.kit.fileexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.SpInputType
import com.didichuxing.doraemonkit.kit.core.TitledBaseFragment
import com.didichuxing.doraemonkit.util.FileUtil
import com.didichuxing.doraemonkit.util.SharedPrefsUtil
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.io.File

/**
 * @author lostjobs created on 2020/6/14
 */
class SpFragment : TitledBaseFragment() {

    private var mSpTableName: String? = null
    private val mSpAdapter by lazy {
        SpAdapter(requireContext())
    }

    override fun onRequestLayout(): Int = R.layout.dk_fragment_sp_show

    private fun spFile() = arguments?.getSerializable(BundleKey.FILE_KEY) as? File

    @SuppressLint("CommitPrefEdits")
    private fun prepareSpBean(): List<SpBean> {
        val spBeanList = mutableListOf<SpBean>()
        val spFile = spFile() ?: return spBeanList
        val spTableName = spFile.name.replace(FileUtil.XML, "")
        mSpTableName = spTableName
        val sp = SharedPrefsUtil.getSharedPrefs(spTableName) ?: return spBeanList
        val keys = sp.all
        if (keys.isEmpty()) return spBeanList
        for (entry in keys.entries) {
            spBeanList.add(SpBean(entry.key, entry.value!!))
        }
        return spBeanList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spBeanList = prepareSpBean()
        if (spBeanList.isEmpty()) {
            showToast(getString(R.string.dk_prefs_is_empty, spFile()?.name ?: ""))
            finish()
            return
        }
        val titleBar = view.findViewById<TitleBar>(R.id.title_bar)
        titleBar.setTitle(mSpTableName)
        titleBar.onTitleBarClickListener = this

        val spList = view.findViewById<RecyclerView>(R.id.rv_sp)
        with(spList) {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            mSpAdapter.doOnSpBeanChanged {
                setUpData(it)
            }
            mSpAdapter.append(spBeanList)
            adapter = mSpAdapter
        }
    }


    private fun setUpData(spBean: SpBean) {
        when (spBean.cls.simpleName) {
            SpInputType.BOOLEAN -> SharedPrefsUtil.putBoolean(mSpTableName, spBean.key, spBean.value as? Boolean
                    ?: false)
            SpInputType.STRING -> SharedPrefsUtil.putString(mSpTableName, spBean.key, spBean.value.toString())
            SpInputType.INTEGER -> SharedPrefsUtil.putInt(mSpTableName, spBean.key, spBean.value as? Int
                    ?: 0)
            SpInputType.FLOAT -> SharedPrefsUtil.putFloat(mSpTableName, spBean.key, spBean.value as? Float
                    ?: 0f)
            SpInputType.LONG -> SharedPrefsUtil.putLong(mSpTableName, spBean.key, spBean.value as? Long
                    ?: 0L)
        }
    }


}
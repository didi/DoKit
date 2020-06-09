package com.didichuxing.doraemonkit.kit.webdoor

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.CacheUtils
import java.util.*

/**
 * Created by guofeng007 on 2020/6/8
 */
class WebDoorManager {
    var webDoorCallback = { context: Context, url: String? ->
        val intent = Intent(context, UniversalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT)
        intent.putExtra(BundleKey.KEY_URL, url)
        context.startActivity(intent)
    }
    private var mHistory: ArrayList<String>? = null


    fun saveHistory(text: String): Boolean {
        if (mHistory == null) {
            mHistory = CacheUtils.readObject(CachesKey.WEB_DOOR_HISTORY) as ArrayList<String>?
        }
        if (mHistory == null) {
            mHistory = ArrayList()
        }
        if (mHistory!!.contains(text)) {
            return false
        }
        if (mHistory!!.size == 5) {
            mHistory!!.removeAt(0)
        }
        mHistory!!.add(text)
        CacheUtils.saveObject(CachesKey.WEB_DOOR_HISTORY, mHistory)
        return true
    }

    val history: ArrayList<String>?
        get() {
            if (mHistory == null) {
                mHistory = CacheUtils.readObject(CachesKey.WEB_DOOR_HISTORY) as ArrayList<String>?
            }
            if (mHistory == null) {
                mHistory = ArrayList()
            }
            return mHistory!!
        }

    fun clearHistory() {
        mHistory!!.clear()
        CacheUtils.saveObject(CachesKey.WEB_DOOR_HISTORY, mHistory)
    }

    private object Holder {
        val INSTANCE = WebDoorManager()
    }

    interface WebDoorCallback {
        fun overrideUrlLoading(context: Context, url: String?)
    }


    companion object {
        private const val TAG = "WebDoorManager"

        @kotlin.jvm.JvmStatic
        val instance: WebDoorManager
            get() = Holder.INSTANCE
    }
}
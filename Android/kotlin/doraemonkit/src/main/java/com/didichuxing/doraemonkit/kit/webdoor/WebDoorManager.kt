package com.didichuxing.doraemonkit.kit.webdoor

import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.UniversalActivity

/**
 * Created by wanglikun on 2018/10/10.
 */
class WebDoorManager {
    var webDoorCallback: ((context: Context, url: String?) -> Unit)? = { context: Context, url: String? ->
        val intent = Intent(context, UniversalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_WEB_DOOR_DEFAULT)
        intent.putExtra(BundleKey.KEY_URL, url)
        context.startActivity(intent)
    }


    private var mHistory: MutableList<String>? = null

    fun removeWebDoorCallback() {
        webDoorCallback = null
    }


    companion object {
        val instance: WebDoorManager = WebDoorManager()
    }
}
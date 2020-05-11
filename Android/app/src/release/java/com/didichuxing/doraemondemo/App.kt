package com.didichuxing.doraemondemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import com.didichuxing.doraemondemo.WebViewActivity
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by zhangweida on 2018/6/22.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        DoraemonKit.disableUpload()
        //是否显示入口icon
        //DoraemonKit.setAwaysShowMainIcon(false);
        DoraemonKit.install(this, "a49842eeebeb1989b3f9565eb12c276b")

        DoraemonKit.setWebDoorCallback(WebDoorManager.WebDoorCallback { context, url ->
            val intent = Intent(this@App, WebViewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(WebViewActivity.KEY_URL, url)
            startActivity(intent)
        })


        Fresco.initialize(this)


        //严格检查模式
        //StrictMode.enableDefaults();
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private const val TAG = "App"
        var leakActivity: Activity? = null
    }
}
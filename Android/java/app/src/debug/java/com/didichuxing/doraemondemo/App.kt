package com.didichuxing.doraemondemo

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.didichuxing.doraemondemo.dokit.DemoKit
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig

/**
 * @author jint
 * @mail 704167880@qq.com
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        DoraemonKit.disableUpload()
        //是否显示入口icon
        // DoraemonKit.setAwaysShowMainIcon(false);


//        val kits: MutableList<AbstractKit> = ArrayList()
//        kits.add(DemoKit())
//        kits.add(DemoKit())
//        kits.add(DemoKit())
//        kits.add(DemoKit())

        val mapKits: LinkedHashMap<String, MutableList<AbstractKit>> = linkedMapOf()
        mapKits["业务专区1"] = mutableListOf<AbstractKit>(DemoKit())
        mapKits["业务专区2"] = mutableListOf<AbstractKit>(DemoKit())

        DoraemonKit.install(this, mapKits = mapKits, productId = "749a0600b5e48dd77cf8ee680be7b1b7")
        //设置加密数据库
        DoraemonKit.setDatabasePass(mapOf("Person.db" to "a_password"))
        val config = ImagePipelineConfig.newBuilder(this)
                .setDiskCacheEnabled(false)
                .build()
        Fresco.initialize(this, config)

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
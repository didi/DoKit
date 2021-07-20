package com.didichuxing.doraemondemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.multidex.MultiDex
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.didichuxing.doraemondemo.dokit.DemoKit
import com.didichuxing.doraemondemo.dokit.TestSimpleDokitFloatViewKit
import com.didichuxing.doraemondemo.dokit.TestSimpleDokitFragmentKit
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKitCallBack
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.lzy.okgo.OkGo
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File

/**
 * @author jint
 * @mail 704167880@qq.com
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //百度地图初始化
        SDKInitializer.initialize(this)
        SDKInitializer.setCoordType(CoordType.BD09LL)
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        //DoraemonKit.disableUpload()
        //是否显示入口icon
        // DoraemonKit.setAwaysShowMainIcon(false);


        val kits: MutableList<AbstractKit> = ArrayList()
        kits.add(DemoKit())
        kits.add(TestSimpleDokitFloatViewKit())
        kits.add(TestSimpleDokitFragmentKit())

        val mapKits: LinkedHashMap<String, List<AbstractKit>> = linkedMapOf()
        mapKits["业务专区1"] = mutableListOf<AbstractKit>().apply {
            add(DemoKit())
            add(TestSimpleDokitFloatViewKit())
            add(TestSimpleDokitFragmentKit())
        }

        mapKits["业务专区2"] = mutableListOf<AbstractKit>(DemoKit())



        DoKit.Builder(this)
            .productId("749a0600b5e48dd77cf8ee680be7b1b7")
//            .productId("277016abcc33bff1e6a4f1afdf14b8e1")
            .disableUpload()
            .customKits(mapKits)
            .fileManagerHttpPort(9001)
            .databasePass(mapOf("Person.db" to "a_password"))
            .mcWSPort(5555)
            .alwaysShowMainIcon(true)
            .callBack(object : DoKitCallBack {
                override fun onCpuCallBack(value: Float, filePath: String) {
                    super.onCpuCallBack(value, filePath)
                }

                override fun onFpsCallBack(value: Float, filePath: String) {
                    super.onFpsCallBack(value, filePath)
                }

                override fun onMemoryCallBack(value: Float, filePath: String) {
                    super.onMemoryCallBack(value, filePath)
                }

                override fun onNetworkCallBack(record: NetworkRecord) {
                    super.onNetworkCallBack(record)
                }
            })
            .netExtInterceptor(object : DokitExtInterceptor.DokitExtInterceptorProxy {
                override fun intercept(chain: Interceptor.Chain): Response {
                    return chain.proceed(chain.request())
                }

            })
            .mcClientProcess(object : McClientProcessor {
                override fun process(
                    activity: Activity?,
                    view: View?,
                    eventType: String,
                    params: Map<String, String>
                ) {
                }

            })
            .build()


        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(CustomInterceptor())
            .cache(Cache(File("${PathUtils.getInternalAppCachePath()}/dokit"), 1024 * 1024 * 100))
            .build()
        OkGo.getInstance().init(this).okHttpClient = client

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
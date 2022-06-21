package com.didichuxing.doraemondemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import androidx.multidex.MultiDex
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemondemo.dokit.DemoKit
import com.didichuxing.doraemondemo.dokit.TestSimpleDokitFloatViewKit
import com.didichuxing.doraemondemo.dokit.TestSimpleDokitFragmentKit
import com.didichuxing.doraemondemo.mc.SlideBar
import com.didichuxing.doraemondemo.module.http.CustomInterceptor
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKitCallBack
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.McClientProcessor
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.util.LogUtils
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
                //测试环境pid
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
                    when (eventType) {
                        "un_lock" -> {
                            ToastUtils.showShort(params["unlock"])
                        }
                        "lock_process" -> {
                            val leftMargin = params["progress"]?.toInt()
                            leftMargin?.let {
                                if (view is SlideBar) {
                                    view.setMarginLeftExtra(it)
                                }
                            }

                        }
                        else -> {

                        }
                    }

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

//        PaymentConfiguration.init(
//            this,
//            "pk_test_TYooMQauvdEDq54NiTphI7jx"
//        )

        //严格检查模式
        //StrictMode.enableDefaults();

        com.didichuxing.doraemonkit.util.LogUtils.getConfig()
            .setLogSwitch(true)
            // 设置是否输出到控制台开关，默认开
            .setConsoleSwitch(true)
            // 设置 log 全局标签，默认为空，当全局标签不为空时，我们输出的 log 全部为该 tag， 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setGlobalTag("Dokit")
            // 设置 log 头信息开关，默认为开
            .setLogHeadSwitch(true)
            // 打印 log 时是否存到文件的开关，默认关
            .setLog2FileSwitch(false)
            // 当自定义路径为空时，写入应用的/cache/log/目录中
            .setDir("")
            // 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
            .setFilePrefix("djx-table-log")
            // 输出日志是否带边框开关，默认开
            .setBorderSwitch(true)
            // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setSingleTagSwitch(false)
            // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setConsoleFilter(LogUtils.V)
            // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(LogUtils.E)
            // log 栈深度，默认为 1
            .setStackDeep(1)
            .stackOffset = 1
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

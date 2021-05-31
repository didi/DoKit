package com.didichuxing.doraemondemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.multidex.MultiDex
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.DoKitCallBack
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.MCInterceptor
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DokitExtInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import com.facebook.drawee.backends.pipeline.Fresco
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by zhangweida on 2018/6/22.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(this)
        SDKInitializer.setCoordType(CoordType.BD09LL)
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        //DoraemonKit.disableUpload()
        //是否显示入口icon
        // DoraemonKit.setAwaysShowMainIcon(false);


        val kits: MutableList<AbstractKit> = ArrayList()
//        kits.add(DemoKit())
//        kits.add(TestSimpleDokitFloatViewKit())
//        kits.add(TestSimpleDokitFragmentKit())

        val mapKits: LinkedHashMap<String, List<AbstractKit>> = linkedMapOf()
//        mapKits["业务专区1"] = mutableListOf<AbstractKit>().apply {
//            add(DemoKit())
//            add(TestSimpleDokitFloatViewKit())
//            add(TestSimpleDokitFragmentKit())
//        }

        //mapKits["业务专区2"] = mutableListOf<AbstractKit>(DemoKit())
        //老的初始化方式
//        DoraemonKit.setDatabasePass(mapOf("Person.db" to "a_password"))
//        DoraemonKit.disableUpload()
//        DoraemonKit.setFileManagerHttpPort(9001)
//        DoraemonKit.setDatabasePass(mapOf("Person.db" to "a_password"))
//        DoraemonKit.setMCIntercept(object : MCInterceptor {
//            override fun onIntercept(
//                view: View,
//                accessibilityEvent: AccessibilityEvent
//            ): Boolean {
//                return false
//            }
//
//            override fun serverParams(
//                view: View,
//                accessibilityEvent: AccessibilityEvent
//            ): Map<String, String> {
//                return mapOf()
//            }
//
//            override fun clientProcess(view: View, params: Map<String, String>): Boolean {
//                return false
//            }
//        })
//        DoraemonKit.setMCWSPort(5555)
//        DoraemonKit.install(this, mapKits = mapKits, productId = "749a0600b5e48dd77cf8ee680be7b1b7")
//


        DoKit.Builder(this)
            .productId("749a0600b5e48dd77cf8ee680be7b1b7")
            .disableUpload()
            .customKits(mapKits)
            .fileManagerHttpPort(9001)
            .databasePass(mapOf("Person.db" to "a_password"))
            .mcWSPort(5555)
            .awaysShowMainIcon(true)
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
            .mcIntercept(object : MCInterceptor {
                override fun onIntercept(
                    view: View,
                    accessibilityEvent: AccessibilityEvent
                ): Boolean {
                    return false
                }

                override fun serverParams(
                    view: View,
                    accessibilityEvent: AccessibilityEvent
                ): Map<String, String> {
                    return mapOf()
                }

                override fun clientProcess(view: View, params: Map<String, String>): Boolean {
                    return false
                }
            })
            .build()



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
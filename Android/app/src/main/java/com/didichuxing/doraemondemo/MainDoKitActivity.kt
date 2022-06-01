package com.didichuxing.doraemondemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import com.didichuxing.doraemondemo.mc.MCActivity
import com.didichuxing.doraemondemo.module.CrashTest
import com.didichuxing.doraemondemo.module.DoKitItemView
import com.didichuxing.doraemondemo.module.MethodCostTest
import com.didichuxing.doraemondemo.module.bigbitmap.BigBitmapActivity
import com.didichuxing.doraemondemo.module.db.DataBaseTest
import com.didichuxing.doraemondemo.module.http.FileUploadTest
import com.didichuxing.doraemondemo.module.http.OkHttpMock
import com.didichuxing.doraemondemo.module.http.RetrofitMock
import com.didichuxing.doraemondemo.module.http.URLConnectionMock
import com.didichuxing.doraemondemo.module.leak.LeakActivity
import com.didichuxing.doraemondemo.old.MainDebugActivityOkhttpV3
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.util.ToastUtils


/**
 * didi Create on 2022/5/25 .
 *
 * Copyright (c) 2022/5/25 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/25 6:05 下午
 * @Description 用一句话说明文件功能
 */

class MainDoKitActivity : BaseStatusBarActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dokit_main)
        val all: ViewGroup = findViewById<ViewGroup>(R.id.all)

        all.children.forEach {
            if (it is DoKitItemView) {
                val item = it as DoKitItemView
                item.setOnClickListener {
                    onItemClick(item, item.itemText)
                }
            }
        }
    }


    private fun onItemClick(itemView: DoKitItemView, text: String) {
        Log.i("TEST", "onItemClick :$text")
        when (text) {
            //工具入口
            "显示/隐藏快捷入口" -> {
                showHideDoKit();
            }
            "打开工具窗口" -> {
                DoKit.showToolPanel()
            }
            //平台工具
            "数据Mock测试" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }
            "OkHttp 模拟请求" -> {
                OkHttpMock.test()
            }
            "UrlConnection 模拟请求" -> {
                URLConnectionMock.get("https://wanandroid.com/user_article/list/0/json")
            }
            "retrofit 模拟请求" -> {
                RetrofitMock.test()
            }
            "一机多控测试" -> {
                startActivity(Intent(this, MCActivity::class.java))
            }
            "自动化测试" -> {
                startActivity(Intent(this, MCActivity::class.java))
            }

            //常用工具
            "日志测试" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }
            "跳转其他Activity" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }

            "系统:WebView" -> {
                startActivity(Intent(this, WebViewSystemActivity::class.java))
            }
            "X5:WebView" -> {
                startActivity(Intent(this, WebViewX5Activity::class.java))
            }

            //LBS
            "位置模拟" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }
            "路径模拟" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }

            //性能工具
            "模拟内存泄漏" -> {
                startActivity(Intent(this, LeakActivity::class.java))
            }

            "模拟耗时函数调用" -> {
                MethodCostTest.test()
            }

            "崩溃模拟" -> {
                CrashTest.test()
            }

            "创建数据库" -> {
                DataBaseTest.test()
            }
            "文件上传模拟" -> {
                FileUploadTest.requestByFile(getActivity(), filesDir, true)
            }
            "文件下载模拟" -> {
                FileUploadTest.requestByFile(getActivity(), filesDir, false)
            }
            "大图检测模拟" -> {
                startActivity(Intent(this, BigBitmapActivity::class.java))
            }
            //视觉工具
            "取色器测试" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }
            "标尺对齐测试" -> {
                startActivity(Intent(this, EmptyActivity::class.java))
            }

            //其他工具
            "旧版页面入口" -> {
                startActivity(Intent(this, MainDebugActivityOkhttpV3::class.java))
            }
            else -> {
                ToastUtils.showShort("$text")
            }

        }
    }


    private fun getActivity(): Activity {
        return this
    }

    private fun showHideDoKit() {
        if (DoKit.isMainIconShow) {
            DoKit.hide()
        } else {
            DoKit.show()
        }
    }

}

package com.didichuxing.doraemondemo.module.http

import android.util.Log
import com.didichuxing.doraemondemo.old.MainDebugActivityOkhttpV3
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject


/**
 * didi Create on 2022/5/27 .
 *
 * Copyright (c) 2022/5/27 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/27 3:22 下午
 * @Description 用一句话说明文件功能
 */

object OkHttpMock {


    fun test() {

        val jsonObject = JSONObject()
        jsonObject.put("c", "cc")
        jsonObject.put("d", "dd")
        OkGo.post<String>("https://wanandroid.com/user_article/list/0/json?b=bb&a=aa")
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.let {
                        Log.i(MainDebugActivityOkhttpV3.TAG, "okhttp====onSuccess===>" + it.body())
                    }
                }

            })

    }

    fun test2() {
        OkGo.post<String>("https://wanandroid.com/user_article/list/0/json?b=bb&a=aa")
            .params("c", "cc")
            .params("d", "dd")
            .execute()

    }


    fun test3() {
        OkGo.get<String>("https://wanandroid.com/user_article/list/0/json?a=aa&b=bb")
            //.upJson(json.toString())
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.let {
                        Log.i(
                            MainDebugActivityOkhttpV3.TAG,
                            "okhttp====onSuccess===>" + it.body()
                        )
                    }
                }

                override fun onError(response: Response<String>?) {
                    response?.let {
                        Log.i(
                            MainDebugActivityOkhttpV3.TAG,
                            "okhttp====onError===>" + it.message()
                        )
                    }
                }

            })
    }

}

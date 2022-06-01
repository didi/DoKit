package com.didichuxing.doraemondemo.module.http

import android.util.Log
import com.didichuxing.doraemondemo.old.MainDebugActivityOkhttpV3
import com.didichuxing.doraemondemo.module.retrofit.GithubService
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitMock {


    private val lifecycleScope = MainScope() + CoroutineName(this.toString())


    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * github 接口
     */
    private var githubService: GithubService? = null

    fun test() {
        githubService = retrofit.create(GithubService::class.java)

        lifecycleScope.launch {
            val result = githubService?.githubUserInfo("jtsky")
            Log.i(MainDebugActivityOkhttpV3.TAG, "result===>${result}")
        }
    }
}

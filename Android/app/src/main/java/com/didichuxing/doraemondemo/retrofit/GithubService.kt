package com.didichuxing.doraemondemo.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/14-17:34
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface GithubService {
    @GET("users/{user}")
    fun githubUserInfo(@Path("user") user: String?): Observable<GithubUserInfo>
}
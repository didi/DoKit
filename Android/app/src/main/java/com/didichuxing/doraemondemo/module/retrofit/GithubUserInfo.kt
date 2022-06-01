package com.didichuxing.doraemondemo.module.retrofit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/14-17:40
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class GithubUserInfo(
        var login: String?,
        var id: Int?,
        var node_id: String?,
        var avatar_url: String?,
        var gravatar_id: String?,
        var url: String?,
        var html_url: String?,
        var followers_url: String?,
        var following_url: String?,
        var gists_url: String?,
        var starred_url: String?,
        var subscriptions_url: String?,
        var organizations_url: String?,
        var repos_url: String?,
        var events_url: String?,
        var received_events_url: String?,
        var type: String?,
        var isSite_admin: Boolean,
        var name: String?,
        var company: Any?,
        var blog: String?,
        var location: String?,
        var email: Any?,
        var hireable: Any?,
        var bio: String?,
        var public_repos: Int,
        var public_gists: Int,
        var followers: Int,
        var following: Int,
        var created_at: String?,
        var updated_at: String?
)

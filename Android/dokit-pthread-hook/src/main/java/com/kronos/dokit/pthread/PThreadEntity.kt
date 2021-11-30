package com.kronos.dokit.pthread

import org.json.JSONObject

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/17
 *
 */
data class PThreadEntity(
    val hash: String, val native: String, val java: String,
    val count: Int, val threads: MutableList<ThreadNameEntity>
) {


}

data class ThreadNameEntity(val tid: String, val name: String)


fun JSONObject.parserThreads(): MutableList<ThreadNameEntity> {
    val jsonArray = optJSONArray("threads")
    val index = jsonArray?.length() ?: 0
    val threads = mutableListOf<ThreadNameEntity>()
    for (index in 0 until index) {
        val thread = jsonArray.optJSONObject(index)
        threads.add(ThreadNameEntity(thread.optString("tid"), thread.optString("name")))
    }
    return threads
}

fun String.parserPThread(): MutableList<PThreadEntity> {
    val jsonObject = JSONObject(this)
    val pThreadArray = jsonObject.optJSONArray("PthreadHook_not_exited")
    val pThreads = mutableListOf<PThreadEntity>()
    pThreadArray?.apply {
        for (index in 0 until length()) {
            val jsonObject = optJSONObject(index)
            pThreads.add(
                PThreadEntity(
                    jsonObject.optString("hash"), jsonObject.optString("native"),
                    jsonObject.optString("java"), jsonObject.optInt("count", 0),
                    jsonObject.parserThreads()
                )
            )
        }
    }
    return pThreads
}


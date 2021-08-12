package com.didichuxing.doraemondemo.comm

import androidx.lifecycle.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/11/21-16:12
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CommViewModel(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        const val KEY_TITLE = "title"
    }

    var title: String?
        get() {
            return state.get(KEY_TITLE)
        }
        set(value) {
            state.set(KEY_TITLE, value)
        }

    fun getTitle(): LiveData<String> {
        return state.getLiveData<String>(KEY_TITLE, "DoKit")
    }

    fun saveTitle(title: String) {

        state.set(KEY_TITLE, title)
    }

}
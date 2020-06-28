package com.didichuxing.doraemonkit.util

import android.content.Context
import android.content.SharedPreferences
import com.blankj.utilcode.util.Utils
import com.didichuxing.doraemonkit.constant.SharedPrefsKey

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: spUtil
 */
object SharedPrefsUtil {
    /**
     * 退出时保存sp状态 需要用commit
     */
    private const val SHARED_PREFS_DORAEMON = "shared_prefs_doraemon"
    private val sharedPrefs: SharedPreferences?
        get() = getSharedPrefs(SHARED_PREFS_DORAEMON)

    fun getSharedPrefs(name: String?): SharedPreferences? {
        return Utils.getApp().getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getString(key: String?, defVal: String?): String? {
        return sharedPrefs!!.getString(key, defVal)
    }

    fun putString(key: String, value: String?) {
        putString(SHARED_PREFS_DORAEMON, key, value)
    }

    fun putString(table: String?, key: String, value: String?) {
        try {
            getSharedPrefs(table)?.let {
                if (key == SHARED_PREFS_DORAEMON) {
                    it.edit().putString(key, value).commit()
                } else {
                    it.edit().putString(key, value).apply()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        putBoolean(SHARED_PREFS_DORAEMON, key, value)
    }

    fun putBoolean(table: String?, key: String, value: Boolean) {
        try {
            getSharedPrefs(table)?.let {
                if (key == SharedPrefsKey.APP_HEALTH) {
                    it.edit().putBoolean(key, value).commit()
                } else {
                    it.edit().putBoolean(key, value).apply()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBoolean(key: String?, defVal: Boolean): Boolean {
        return sharedPrefs!!.getBoolean(key, defVal)
    }

    fun putInt(key: String?, value: Int) {
        putInt(SHARED_PREFS_DORAEMON, key, value)
    }

    fun putInt(table: String?, key: String?, value: Int?) {
        try {
            getSharedPrefs(table)!!.edit().putInt(key, value!!).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getInt(key: String?, defVal: Int): Int {
        return sharedPrefs!!.getInt(key, defVal)
    }

    fun putFloat(table: String?, key: String?, value: Float?) {
        try {
            getSharedPrefs(table)!!.edit().putFloat(key, value!!).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun putFloat(key: String?, value: Float?) {
        try {
            getSharedPrefs(SHARED_PREFS_DORAEMON)!!.edit().putFloat(key, value!!).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFloat(key: String?, value: Float?): Float {
        return sharedPrefs!!.getFloat(key, value!!)
    }

    fun putLong(table: String?, key: String?, value: Long?) {
        try {
            getSharedPrefs(table)!!.edit().putLong(key, value!!).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
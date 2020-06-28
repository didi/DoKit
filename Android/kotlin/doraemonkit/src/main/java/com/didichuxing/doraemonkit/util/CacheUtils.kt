package com.didichuxing.doraemonkit.util

import com.blankj.utilcode.util.Utils
import java.io.*

/**
 * Created by guofeng007 on 2020/6/8
 */
object CacheUtils {
    private const val TAG = "CacheUtils"
    fun saveObject(key: String, ser: Serializable?): Boolean {
        val file = File(Utils.getApp().cacheDir.toString() + "/" + key)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return saveObject(ser, file)
    }

    fun readObject(key: String): Serializable? {
        val file = File(Utils.getApp().cacheDir.toString() + "/" + key)
        return readObject(file)
    }

    @JvmStatic
    fun saveObject(ser: Serializable?, file: File?): Boolean {
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        return try {
            fos = FileOutputStream(file)
            oos = ObjectOutputStream(fos)
            oos.writeObject(ser)
            oos.flush()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            if (oos != null) {
                try {
                    oos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun readObject(file: File?): Serializable? {
        if (file == null || !file.exists() || file.isDirectory) {
            return null
        }
        var fis: FileInputStream? = null
        var ois: ObjectInputStream? = null
        return try {
            fis = FileInputStream(file)
            ois = ObjectInputStream(fis)
            ois.readObject() as Serializable
        } catch (e: IOException) {
            if (e is InvalidClassException) {
                file.delete()
            }
            e.printStackTrace()
            null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        } finally {
            if (fis != null) {
                try {
                    fis.close()
                } catch (e: IOException) {
                    LogHelper.e(TAG, e.toString())
                }
            }
            if (ois != null) {
                try {
                    ois.close()
                } catch (e: IOException) {
                    LogHelper.e(TAG, e.toString())
                }
            }
        }
    }
}
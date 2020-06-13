package com.didichuxing.doraemonkit.kit.largepicture

import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.isLargeImgOpen
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import java.text.DecimalFormat
import java.util.*

/**
 * @author:  maple
 * @time:  2020/6/8 - 13:58
 * @desc: 大图检测管理类
 */
class LargePictureManager private constructor() {
    private var fileThreshold: Double = PerformanceSpInfoConfig.getLargeImgFileThreshold(FILE_DEFAULT_THRESHOLD)
    private var memoryThreshold: Double = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(MEMORY_DEFAULT_THRESHOLD)
    private val TAG = "LargePictureManager"
    private val mDecimalFormat = DecimalFormat("0.00")
    fun setFileThreshold(fileThreshold: Double) {
        this.fileThreshold = fileThreshold
    }

    fun setMemoryThreshold(memoryThreshold: Double) {
        this.memoryThreshold = memoryThreshold
    }

    companion object {
        /**
         * 保存大图信息
         */
        val LARGE_IMAGE_INFO_MAP = mutableMapOf<String, LargeImageInfo>()
        var MEMORY_DEFAULT_THRESHOLD = 1.0f
        var FILE_DEFAULT_THRESHOLD = 150.0f
        fun getInstance(): LargePictureManager {
            return Holder.INSTANCE;
        }

    }

    private object Holder {
        val INSTANCE = LargePictureManager()
    }

    /**
     * @param url
     * @param size
     */
    fun process(url: String, size: Int) {
        if (ActivityUtils.getTopActivity() is UniversalActivity) {
            return
        }
        if (isLargeImgOpen()) { //转化成kb
            saveImageInfo(url, (size / 1024.0))
        }
    }

    /**
     * 保存网络图片信息
     *
     * @param url
     * @param fileSize
     */
    private fun saveImageInfo(url: String, fileSize: Double) {
        if (ActivityUtils.getTopActivity() is UniversalActivity) {
            return
        }
        val largeImageInfo: LargeImageInfo?
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP[url]
        } else {
            largeImageInfo = LargeImageInfo()
            LARGE_IMAGE_INFO_MAP[url] = largeImageInfo
            largeImageInfo.setUrl(url)
        }
        largeImageInfo!!.setFileSize(fileSize)
    }

    /**
     * 保存在内部里的图片信息
     *
     * @param url
     * @param memorySize
     * @param width
     * @param height
     */
    fun saveImageInfo(url: String, memorySize: Double, width: Int, height: Int, framework: String?) {
        if (ActivityUtils.getTopActivity() is UniversalActivity) {
            return
        }
        val largeImageInfo: LargeImageInfo?
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP[url]
        } else {
            largeImageInfo = LargeImageInfo()
            LARGE_IMAGE_INFO_MAP[url] = largeImageInfo
            largeImageInfo.setUrl(url)
        }
        largeImageInfo!!.setMemorySize(memorySize)
        largeImageInfo.setWidth(width)
        largeImageInfo.setHeight(height)
        largeImageInfo.setFramework(framework!!)
    }
}
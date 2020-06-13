package com.didichuxing.doraemonkit.kit.largepicture

/**
 * @author:  maple
 * @time:  2020/6/8 - 14:01
 * @desc: 大图信息
 */
class LargeImageInfo {
    private var framework = "other"
    private var url: String? = null
    private var fileSize = 0.0
    private var memorySize = 0.0
    private var width = 0
    private var height = 0

    fun setUrl(url: String?) {
        this.url = url
    }

    fun getFileSize(): Double {
        return fileSize
    }

    fun setFileSize(fileSize: Double) {
        this.fileSize = fileSize
    }

    fun setMemorySize(memorySize: Double) {
        this.memorySize = memorySize
    }

    fun setWidth(width: Int) {
        this.width = width
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    fun getUrl(): String? {
        return url
    }

    fun getMemorySize(): Double {
        return memorySize
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }


    fun getFramework(): String? {
        return framework
    }

    fun setFramework(framework: String) {
        this.framework = framework
    }

    override fun toString(): String {
        return "LargeImageInfo{" +
                "framework='" + framework + '\'' +
                ", url='" + url + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", memorySize='" + memorySize + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}'
    }
}
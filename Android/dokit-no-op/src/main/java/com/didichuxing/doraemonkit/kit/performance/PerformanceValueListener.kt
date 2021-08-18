package com.didichuxing.doraemonkit.kit.performance
/**
 * Author: xuweiyu
 * Date: 5/12/21
 * Email: wizz.xu@outlook.com
 * Description: cpu 内存 FPS 的回调监听
 */
interface PerformanceValueListener {
    fun onGetMemory(value: Float)
    fun onGetCPU(value: Float)
    fun onGetFPS(value: Float)
}
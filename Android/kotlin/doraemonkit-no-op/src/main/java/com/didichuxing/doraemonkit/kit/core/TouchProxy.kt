package com.didichuxing.doraemonkit.kit.core

/**
 * @author wanglikun
 * touch 事件代理 解决点击和触摸事件的冲突
 */
class TouchProxy(eventListener: OnTouchEventListener?) {


    interface OnTouchEventListener {
        fun onMove(x: Int, y: Int, dx: Int, dy: Int)
        fun onUp(x: Int, y: Int)
        fun onDown(x: Int, y: Int)
    }
}
package com.didichuxing.doraemonkit

import android.app.Application
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.MCInterceptor
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager
import java.lang.NullPointerException

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/7/21-16:00
 * 描    述：DoKit 入口类
 * 修订历史：
 * ================================================
 */
public class DoKit {
    companion object {


        /**
         * 主icon是否处于显示状态
         */
        @JvmStatic
        val isMainIconShow: Boolean
            get() = false


        /**
         * 显示主icon
         */
        @JvmStatic
        fun show() {
        }

        /**
         * 直接显示工具面板页面
         */
        @JvmStatic
        fun showToolPanel() {
        }

        /**
         * 直接隐藏工具面板
         */
        @JvmStatic
        fun hideToolPanel() {
        }

        /**
         * 隐藏主icon
         */
        @JvmStatic
        fun hide() {
        }
    }


    class Builder(val app: Application) {


        fun productId(productId: String): Builder {
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(mapKits: LinkedHashMap<String, List<AbstractKit>>): Builder {
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(listKits: List<AbstractKit>): Builder {
            return this
        }

        /**
         * H5任意门全局回调
         */
        fun webDoorCallback(callback: WebDoorManager.WebDoorCallback): Builder {
            return this
        }

        /**
         * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
         */
        fun disableUpload(): Builder {
            return this
        }

        fun debug(debug: Boolean): Builder {
            return this
        }

        /**
         * 是否显示主入口icon
         */
        fun awaysShowMainIcon(awaysShow: Boolean): Builder {
            return this
        }

        /**
         * 设置加密数据库密码
         */
        fun databasePass(map: Map<String, String>): Builder {
            return this
        }

        /**
         * 设置文件管理助手http端口号
         */
        fun fileManagerHttpPort(port: Int): Builder {
            return this
        }

        /**
         * 一机多控端口号
         */
        fun mcWSPort(port: Int): Builder {
            return this
        }

        /**
         * 一机多控自定义拦截器
         */
        fun mcIntercept(interceptor: MCInterceptor): Builder {
            return this
        }

        /**
         * 设置dokit的性能监控全局回调
         */
        fun callBack(callback: DoKitCallBack): Builder {
            return this
        }

        fun build() {
        }
    }
}
package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.didichuxing.doraemonkit.constant.DoKitConstant

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DeviceInfoAction {
    fun deviceInfoRes(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            this["code"] = 200
            val data = mutableMapOf<String, String>().apply {
                this["deviceName"] = "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}"
                this["deviceId"] = DeviceUtils.getUniqueDeviceId()
                this["deviceIp"] = "${NetworkUtils.getIpAddressByWifi()}:${DoKitConstant.FILE_MANAGER_HTTP_PORT}"
            }
            this["data"] = data
        }
    }

}
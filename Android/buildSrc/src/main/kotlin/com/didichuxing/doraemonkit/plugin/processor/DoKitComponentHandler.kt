package com.didichuxing.doraemonkit.plugin.processor

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/2/21-17:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
const val ATTR_NAME = "android:name"

const val MANIFEST_ATTR_NAME = "package"

class DoKitComponentHandler : DefaultHandler() {
    var appPackageName: String = ""
    val applications = mutableSetOf<String>()
    val activities = mutableSetOf<String>()
    val services = mutableSetOf<String>()
    val providers = mutableSetOf<String>()
    val receivers = mutableSetOf<String>()

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        val name: String = attributes.getValue(ATTR_NAME) ?: ""

        val packageName: String = attributes.getValue(MANIFEST_ATTR_NAME) ?: ""

        when (qName) {
            "manifest" -> {
                appPackageName = packageName
            }
            "application" -> {
                applications.add(name)
            }
            "activity" -> {
                activities.add(name)
            }
            "service" -> {
                services.add(name)
            }
            "provider" -> {
                providers.add(name)
            }
            "receiver" -> {
                receivers.add(name)
            }

        }
    }

}

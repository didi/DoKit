package com.didichuxing.doraemonkit.kit.blockmonitor.core

import android.content.Context
import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo
import com.didichuxing.doraemonkit.util.SystemUtil.obtainProcessName

object BlockCanaryUtils {
    private var sProcessName: String? = null
    private var sProcessNameFirstGetFlag = false
    private const val CURRENT_PACKAGE = "com.didichuxing.doraemonkit"
    fun concernStackString(context: Context, blockInfo: BlockInfo): String {
        for (stackEntry in blockInfo.threadStackEntries) {
            if (!TextUtils.isEmpty(stackEntry)) {
                val lines = stackEntry.split(BlockInfo.SEPARATOR).toTypedArray()
                for (line in lines) {
                    val keyStackString = concernStackString(context, line)
                    if (keyStackString != null) {
                        return keyStackString
                    }
                }
                return classSimpleName(lines[0])
            }
        }
        return ""
    }

    fun isBlockInfoValid(blockInfo: BlockInfo): Boolean {
        var isValid = !TextUtils.isEmpty(blockInfo.timeStart)
        isValid = isValid && blockInfo.timeCost >= 0
        return isValid
    }

    private fun concernStackString(context: Context, line: String): String? {
        if (!sProcessNameFirstGetFlag) {
            sProcessNameFirstGetFlag = true
            sProcessName = obtainProcessName(context)
        }
        return if (line.startsWith(sProcessName!!) || line.startsWith(CURRENT_PACKAGE)) {
            classSimpleName(line)
        } else null
    }

    private fun classSimpleName(stackLine: String): String {
        val index1 = stackLine.indexOf('(')
        val index2 = stackLine.indexOf(')')
        return if (index1 >= 0 && index2 >= 0) {
            stackLine.substring(index1 + 1, index2)
        } else stackLine
    }
}
package com.didichuxing.doraemonkit.kit.core.group

import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.group.DokitGroup
import com.didichuxing.doraemonkit.util.DokitUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-10:48
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DefaultDokitGroup : AbsDokitGroup() {
    override fun groupName(): String {
        return DokitUtil.getString(R.string.dk_category_biz)
    }
}
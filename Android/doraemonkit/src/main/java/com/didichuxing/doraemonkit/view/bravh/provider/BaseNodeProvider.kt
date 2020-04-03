package com.didichuxing.doraemonkit.view.bravh.provider

import com.didichuxing.doraemonkit.view.bravh.BaseNodeAdapter
import com.didichuxing.doraemonkit.view.bravh.entity.node.BaseNode

abstract class BaseNodeProvider : BaseItemProvider<BaseNode>() {

    override fun getAdapter(): BaseNodeAdapter? {
        return super.getAdapter() as? BaseNodeAdapter
    }

}
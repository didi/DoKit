package com.didichuxing.doraemonkit.widget.bravh.provider

import com.didichuxing.doraemonkit.widget.bravh.BaseNodeAdapter
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode

abstract class BaseNodeProvider : BaseItemProvider<BaseNode>() {

    override fun getAdapter(): BaseNodeAdapter? {
        return super.getAdapter() as? BaseNodeAdapter
    }

}
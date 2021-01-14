package com.didichuxing.doraemonkit.widget.brvah.provider

import com.didichuxing.doraemonkit.widget.brvah.BaseNodeAdapter
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode

abstract class BaseNodeProvider : BaseItemProvider<BaseNode>() {

    override fun getAdapter(): BaseNodeAdapter? {
        return super.getAdapter() as? BaseNodeAdapter
    }

}
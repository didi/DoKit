package com.didichuxing.doraemonkit.kit.fileexplorer

import com.didichuxing.doraemonkit.constant.SpInputType

/**
 * @author lostjobs created on 2020/6/14
 */
class SpBean(val key: String, var value: Any, val cls: Class<*>) {
    constructor(key: String, value: Any) : this(key, value, value::class.java)

    fun toDefaultClass(string: String): Any {
        this.value = when(cls.simpleName){
            SpInputType.BOOLEAN -> string.toBoolean()
            SpInputType.INTEGER -> string.toInt()
            SpInputType.LONG -> string.toLong()
            SpInputType.FLOAT -> string.toFloat()
            else -> string
        }
        return value
    }
}
package com.didichuxing.doraemonkit.plugin.extension

import org.gradle.api.Action

/**
 * Created by jint on 07/10/2018.
 */
open class DoKitExt(
        //var dokitPluginSwitch: Boolean = true,
        var comm: CommExt = CommExt(),
        var slowMethod: SlowMethodExt = SlowMethodExt()) {


    //方法名必须和插件配置一直才能进行反射注入
//    fun dokitPluginSwitch(dokitPluginSwitch: Boolean) {
//        this.dokitPluginSwitch = dokitPluginSwitch
//    }

    /**
     * 让comm 支持 DSL 语法
     *
     * @param action
     */
    fun comm(action: Action<CommExt>) {
        action.execute(comm)
    }

    /**
     * 让slowMethod 支持 DSL 语法
     *
     * @param action
     */
    fun slowMethod(action: Action<SlowMethodExt>) {
        action.execute(slowMethod)
    }

    override fun toString(): String {
        return "DoKitExt(comm=$comm, slowMethod=$slowMethod)"
    }


}
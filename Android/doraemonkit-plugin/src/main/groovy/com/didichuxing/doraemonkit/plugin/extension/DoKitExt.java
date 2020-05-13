package com.didichuxing.doraemonkit.plugin.extension;

import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by jint on 07/10/2018.
 */
public class DoKitExt {

    public RunVariant runVariant = RunVariant.DEBUG;
    public boolean duplcatedClassSafeMode = false;
    /**
     * dokit 插件开关 字段权限必须为public 否则无法进行赋值
     */
    public boolean dokitPluginSwitch = true;


    /**
     * dokit 编译日志开关 字段权限必须为public 否则无法进行赋值
     */
    public boolean dokitLogSwitch = false;

    public void dokitPluginSwitch(boolean dokitPluginSwitch) {
        this.dokitPluginSwitch = dokitPluginSwitch;
    }


    public void dokitLogSwitch(boolean dokitLogSwitch) {
        this.dokitLogSwitch = dokitLogSwitch;
    }


    public CommExt comm = new CommExt();

    public SlowMethodExt slowMethod = new SlowMethodExt();


    /**
     * 让comm 支持 DSL 语法
     *
     * @param action
     */
    public void comm(Action<CommExt> action) {
        action.execute(comm);
    }

    /**
     * 让slowMethod 支持 DSL 语法
     *
     * @param action
     */
    public void slowMethod(Action<SlowMethodExt> action) {
        action.execute(slowMethod);
    }


    @Override
    public String toString() {
        return "DoKitExt{" +
                "dokitPluginSwitch=" + dokitPluginSwitch +
                ", dokitLogSwitch=" + dokitLogSwitch +
                ", commExt=" + comm +
                ", slowMethodExt=" + slowMethod +
                '}';
    }
}

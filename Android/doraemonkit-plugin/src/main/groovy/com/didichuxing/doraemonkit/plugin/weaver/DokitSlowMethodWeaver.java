package com.didichuxing.doraemonkit.plugin.weaver;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DoKitExtUtil;
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitSlowMethodClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt.STRATEGY_NORMAL;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-14:01
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitSlowMethodWeaver extends BaseWeaver {
    private DoKitExt dokitExtension;

    private AppExtension appExtension;

    public DokitSlowMethodWeaver(AppExtension appExtension) {
        this.appExtension = appExtension;
    }

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }
        this.dokitExtension = (DoKitExt) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        boolean isOpen = DoKitExtUtil.getInstance().dokitPluginSwitchOpen() &&
                DoKitExtUtil.getInstance().getSlowMethodExt().methodSwitch &&
                DoKitExtUtil.getInstance().getSlowMethodExt().strategy == STRATEGY_NORMAL;

        if (isOpen) {
            return new DokitSlowMethodClassAdapter(classWriter);
        } else {
            return super.wrapClassWriter(classWriter);
        }

    }
}

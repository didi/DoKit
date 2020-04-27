package com.didichuxing.doraemonkit.plugin.weaver;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtUtil;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitMethodStackClassAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitSlowMethodClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static com.didichuxing.doraemonkit.plugin.DokitExtension.SlowMethodConfig.STRATEGY_NORMAL;
import static com.didichuxing.doraemonkit.plugin.DokitExtension.SlowMethodConfig.STRATEGY_STACK;

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
    private DokitExtension dokitExtension;

    private AppExtension appExtension;

    public DokitSlowMethodWeaver(AppExtension appExtension) {
        this.appExtension = appExtension;
    }

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }
        this.dokitExtension = (DokitExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        boolean isOpen = DokitExtUtil.getInstance().dokitPluginSwitchOpen() &&
                DokitExtUtil.getInstance().getSlowMethodConfig().methodSwitch &&
                DokitExtUtil.getInstance().getSlowMethodConfig().strategy == STRATEGY_NORMAL;

        if (isOpen) {
            return new DokitSlowMethodClassAdapter(classWriter);
        } else {
            return super.wrapClassWriter(classWriter);
        }

    }
}

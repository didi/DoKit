package com.didichuxing.doraemonkit.plugin.weaver;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtUtil;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitCommClassAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitSlowMethodClassAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitUrlConnectionClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static com.didichuxing.doraemonkit.plugin.DokitExtension.SlowMethodConfig.STRATEGY_NORMAL;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-14:01
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitUrlConnectionWeaver extends BaseWeaver {
    private DokitExtension dokitExtension;

    private AppExtension appExtension;

    public DokitUrlConnectionWeaver(AppExtension appExtension) {
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
                DokitExtUtil.getInstance().getCommConfig().networkSwitch;
        if (isOpen) {
            return new DokitUrlConnectionClassAdapter(classWriter);
        } else {
            return super.wrapClassWriter(classWriter);
        }
    }
}

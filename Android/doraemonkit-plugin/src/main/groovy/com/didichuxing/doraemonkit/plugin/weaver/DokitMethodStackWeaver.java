package com.didichuxing.doraemonkit.plugin.weaver;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DoKitExtUtil;
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitMethodStackClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt.STRATEGY_STACK;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-14:01
 * 描    述：dokit 通用weave
 * 修订历史：该对象只会创建一次
 * ================================================
 */
public class DokitMethodStackWeaver extends BaseWeaver {
    private DoKitExt dokitExtension;

    private AppExtension appExtension;
    private int level;

    public DokitMethodStackWeaver(AppExtension appExtension, int level) {
        this.appExtension = appExtension;
        this.level = level;
    }

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }
        this.dokitExtension = (DoKitExt) extension;
    }

    /**
     * 该方法会回调n次
     *
     * @param classWriter
     * @return
     */
    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        boolean isOpen = DoKitExtUtil.getInstance().dokitPluginSwitchOpen() &&
                DoKitExtUtil.getInstance().getSlowMethodExt().methodSwitch &&
                DoKitExtUtil.getInstance().getSlowMethodExt().strategy == STRATEGY_STACK;

        if (isOpen) {
            return new DokitMethodStackClassAdapter(classWriter, level);
        } else {
            return super.wrapClassWriter(classWriter);
        }
    }
}

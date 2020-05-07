package com.didichuxing.doraemonkit.plugin.weaver;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitBigImageClassAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.DokitThreadClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
/**
 * ================================================
 * 作    者：maple
 * 版    本：1.0
 * 创建日期：2020-04-28
 * 描    述：Dokit 线程日志 字节码插装
 * 修订历史：
 * ================================================
 */
public class DokitThreadWeaver extends BaseWeaver {
    private DokitExtension dokitExtension;

    private AppExtension appExtension;

    public DokitThreadWeaver(AppExtension appExtension) {
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
        //返回指定的ClassVisitor
        return new DokitThreadClassAdapter(classWriter);
    }
}
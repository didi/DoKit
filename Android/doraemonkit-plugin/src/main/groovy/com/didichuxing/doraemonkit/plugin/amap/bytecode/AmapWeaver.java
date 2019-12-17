package com.didichuxing.doraemonkit.plugin.amap.bytecode;

import com.didichuxing.doraemonkit.plugin.amap.AmapExtension;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-13-14:01
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AmapWeaver extends BaseWeaver {
    private AmapExtension flagExtension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }
        this.flagExtension = (AmapExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        //返回指定的ClassVisitor
        return new AmapClassAdapter(classWriter);
    }
}

package com.didichuxing.doraemonkit.plugin.urlconnection.bytecode;

import com.didichuxing.doraemonkit.plugin.urlconnection.UrlConnectionExtension;
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
public class UrlConnectionWeaver extends BaseWeaver {
    private UrlConnectionExtension flagExtension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) {
            return;
        }
        this.flagExtension = (UrlConnectionExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        //返回指定的ClassVisitor
        return new UrlConnectionClassAdapter(classWriter);
    }
}

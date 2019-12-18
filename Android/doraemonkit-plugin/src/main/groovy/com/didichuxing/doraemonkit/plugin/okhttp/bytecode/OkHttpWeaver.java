package com.didichuxing.doraemonkit.plugin.okhttp.bytecode;

import com.didichuxing.doraemonkit.plugin.okhttp.OkHttpExtension;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Created by 金台 on 12/12/2019.
 *
 */
public final class OkHttpWeaver extends BaseWeaver {

    private OkHttpExtension okHttpHunterExtension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) return;
        this.okHttpHunterExtension = (OkHttpExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        //返回指定的ClassVisitor
        return new OkHttpClassAdapter(classWriter, this.okHttpHunterExtension.weaveEventListener);
    }

}

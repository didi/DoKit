package com.didichuxing.doraemonkit.plugin.platformhttp.bytecode;

import com.didichuxing.doraemonkit.plugin.platformhttp.PlatformHttpExtension;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Created by 金台 on 12/12/2019.
 *
 */
public final class PlatformHttpWeaver extends BaseWeaver {

    private PlatformHttpExtension okHttpHunterExtension;

    @Override
    public void setExtension(Object extension) {
        if (extension == null) return;
        this.okHttpHunterExtension = (PlatformHttpExtension) extension;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        //返回指定的ClassVisitor
        return new PlatformHttpClassAdapter(classWriter, this.okHttpHunterExtension.weaveEventListener);
    }

}

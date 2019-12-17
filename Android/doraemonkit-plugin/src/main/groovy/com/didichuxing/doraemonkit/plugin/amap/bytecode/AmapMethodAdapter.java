package com.didichuxing.doraemonkit.plugin.amap.bytecode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.reflect.Modifier;

/**
 * Only weave com/didichuxing/doraemonkit/aop/OkHttpHook  installInterceptor method
 * Created by jint on 13/12/2019.
 */
public final class AmapMethodAdapter extends LocalVariablesSorter implements Opcodes {

    AmapMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitCode() {
        //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
        mv.visitTypeInsn(NEW, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy", "<init>", "(Lcom/amap/api/location/AMapLocationListener;)V", false);
        mv.visitVarInsn(ASTORE, 1);
        super.visitCode();
    }

}

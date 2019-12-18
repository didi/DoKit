package com.didichuxing.doraemonkit.plugin.bytecode.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave com/didichuxing/doraemonkit/aop/OkHttpHook  installInterceptor method
 * Created by jint on 13/12/2019.
 */
public final class AmapLocationMethodAdapter extends LocalVariablesSorter implements Opcodes {

    public AmapLocationMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitCode() {
        //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
        mv.visitTypeInsn(NEW, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy");
        mv.visitInsn(DUP);
        //访问第一个参数
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy", "<init>", "(Lcom/amap/api/location/AMapLocationListener;)V", false);
        //对第一个参数进行重新赋值
        mv.visitVarInsn(ASTORE, 1);
        super.visitCode();
    }

}

package com.didichuxing.doraemonkit.plugin.bytecode.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by jint on 13/12/2019.
 */
public final class TencentLocationMethodAdapter extends LocalVariablesSorter implements Opcodes {

    public TencentLocationMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitCode() {
        mv.visitTypeInsn(NEW, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy");
        mv.visitInsn(DUP);
        //访问第二个参数
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy", "<init>", "(Lcom/tencent/map/geolocation/TencentLocationListener;)V", false);
        //对第二个参数进行重新赋值
        mv.visitVarInsn(ASTORE, 2);
        super.visitCode();
    }

}

package com.didichuxing.doraemonkit.plugin.bytecode.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by jint on 13/12/2019.
 */
public final class BaiduLocationMethodAdapter extends LocalVariablesSorter implements Opcodes {

    public BaiduLocationMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitCode() {
        //访问第一个参数
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/BDLocationUtil", "proxy", "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;", false);
        //对第一个参数进行重新赋值
        mv.visitVarInsn(ASTORE, 1);
        super.visitCode();
    }

}
